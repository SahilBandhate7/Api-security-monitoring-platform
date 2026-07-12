package com.apiscan.organization;

import com.apiscan.common.enums.UserRole;
import com.apiscan.common.utils.SlugUtil;
import com.apiscan.domain.OrgMember;
import com.apiscan.domain.Organization;
import com.apiscan.domain.User;
import com.apiscan.organization.dto.CreateOrgRequest;
import com.apiscan.organization.dto.InviteMemberRequest;
import com.apiscan.organization.dto.OrgResponse;
import com.apiscan.repository.OrgMemberRepository;
import com.apiscan.repository.OrganizationRepository;
import com.apiscan.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final UserRepository userRepository;
    private final com.apiscan.repository.OrganizationInviteRepository orgInviteRepository;

    @Transactional
    public OrgResponse createOrganization(String userId, CreateOrgRequest request) {
        String slug = SlugUtil.toSlug(request.name());

        // Ensure slug is unique
        int counter = 0;
        String baseSlug = slug;
        while (organizationRepository.existsBySlug(slug)) {
            counter++;
            slug = baseSlug + "-" + counter;
        }

        Organization org = new Organization();
        org.setName(request.name());
        org.setSlug(slug);
        organizationRepository.save(org);

        // Add creator as OWNER
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        OrgMember member = new OrgMember();
        member.setUser(user);
        member.setOrganization(org);
        member.setRole(UserRole.OWNER);
        orgMemberRepository.save(member);

        log.info("Organization created: {} by user {}", org.getSlug(), userId);
        return mapToResponse(org);
    }

    @Transactional(readOnly = true)
    public OrgResponse getOrganization(String orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        return mapToResponse(org);
    }

    @Transactional
    public OrgResponse updateOrganization(String orgId, String name) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        if (name != null && !name.isBlank()) {
            org.setName(name);
        }
        organizationRepository.save(org);
        return mapToResponse(org);
    }

    @Transactional
    public void deleteOrganization(String orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        organizationRepository.delete(org);
        log.info("Organization deleted: {}", orgId);
    }

    @Transactional
    public void inviteMember(String orgId, InviteMemberRequest request) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        UserRole role = UserRole.TESTER;
        if (request.role() != null && !request.role().isBlank()) {
            role = UserRole.valueOf(request.role().toUpperCase());
        }

        // Check member limit
        long currentMembers = orgMemberRepository.countByOrganizationId(orgId);
        if (currentMembers >= org.getTier().getMaxMembers()) {
            throw new IllegalStateException(
                    "Organization has reached maximum member limit for " + org.getTier() + " plan");
        }
        
        // Prevent re-inviting if already a member
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            if (orgMemberRepository.existsByUserIdAndOrganizationId(user.getId(), orgId)) {
                throw new IllegalStateException("User is already an active member of this organization");
            }
        });

        // Generate Invite
        com.apiscan.domain.OrganizationInvite invite = new com.apiscan.domain.OrganizationInvite();
        invite.setOrganization(org);
        invite.setEmail(request.email());
        invite.setRole(role);
        invite.setToken(java.util.UUID.randomUUID().toString());
        invite.setExpiresAt(java.time.LocalDateTime.now().plusDays(7));
        invite.setStatus("PENDING");
        
        orgInviteRepository.save(invite);

        // Simulated Dispatch
        log.info("📧 [SIMULATED EMAIL DISPATCH] Invited {} to {}. Accept Link: http://localhost:3000/invite/{}", 
                  request.email(), org.getName(), invite.getToken());
    }

    @Transactional
    public OrgResponse acceptInvite(String token, String currentUserId) {
        com.apiscan.domain.OrganizationInvite invite = orgInviteRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"));
            
        if (!"PENDING".equals(invite.getStatus())) {
            throw new IllegalStateException("This invitation has already been processed or expired.");
        }
        
        if (invite.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            invite.setStatus("EXPIRED");
            orgInviteRepository.save(invite);
            throw new IllegalStateException("This invitation has expired.");
        }
        
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new EntityNotFoundException("Session User not found"));
            
        // Optionally lock invitation strictly to the sent email:
        // if (!user.getEmail().equalsIgnoreCase(invite.getEmail())) throw new IllegalStateException("Email mismatch");

        if (orgMemberRepository.existsByUserIdAndOrganizationId(user.getId(), invite.getOrganization().getId())) {
            invite.setStatus("ACCEPTED");
            orgInviteRepository.save(invite);
            throw new IllegalStateException("You are already an active member of this organization.");
        }

        // Mount Member Status
        OrgMember member = new OrgMember();
        member.setUser(user);
        member.setOrganization(invite.getOrganization());
        member.setRole(invite.getRole());
        orgMemberRepository.save(member);
        
        // Burn Token
        invite.setStatus("ACCEPTED");
        orgInviteRepository.save(invite);
        
        log.info("Member {} successfully accepted invite to org {}", user.getEmail(), invite.getOrganization().getId());
        
        return getOrganization(invite.getOrganization().getId());
    }

    @Transactional(readOnly = true)
    public List<OrgResponse.MemberInfo> getMembers(String orgId) {
        List<OrgMember> members = orgMemberRepository.findAllByOrganizationId(orgId);
        return members.stream()
                .map(m -> new OrgResponse.MemberInfo(
                        m.getUser().getId(),
                        m.getUser().getEmail(),
                        m.getUser().getName(),
                        m.getRole().name(),
                        m.getInvitedAt()))
                .toList();
    }

    @Transactional
    public void updateMemberRole(String orgId, String userId, String newRole) {
        OrgMember member = orgMemberRepository.findByUserIdAndOrganizationId(userId, orgId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        if (member.getRole() == UserRole.OWNER) {
            throw new IllegalStateException("Cannot change the owner's role");
        }

        member.setRole(UserRole.valueOf(newRole.toUpperCase()));
        orgMemberRepository.save(member);
    }

    @Transactional
    public void removeMember(String orgId, String userId) {
        OrgMember member = orgMemberRepository.findByUserIdAndOrganizationId(userId, orgId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        if (member.getRole() == UserRole.OWNER) {
            throw new IllegalStateException("Cannot remove the organization owner");
        }

        orgMemberRepository.delete(member);
        log.info("Member {} removed from org {}", userId, orgId);
    }

    @Transactional(readOnly = true)
    public List<OrgResponse> getUserOrganizations(String userId) {
        List<OrgMember> memberships = orgMemberRepository.findAllByUserId(userId);
        return memberships.stream()
                .map(m -> mapToResponse(m.getOrganization()))
                .toList();
    }

    private OrgResponse mapToResponse(Organization org) {
        List<OrgResponse.MemberInfo> members = orgMemberRepository.findAllByOrganizationId(org.getId())
                .stream()
                .map(m -> new OrgResponse.MemberInfo(
                        m.getUser().getId(),
                        m.getUser().getEmail(),
                        m.getUser().getName(),
                        m.getRole().name(),
                        m.getInvitedAt()))
                .toList();

        return new OrgResponse(
                org.getId(), org.getName(), org.getSlug(), org.getTier(),
                org.getMonthlyQuota(), org.getUsedQuota(), org.getCreatedAt(), members);
    }
}
