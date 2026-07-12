package com.apiscan.organization;

import com.apiscan.common.ApiResponse;
import com.apiscan.common.enums.UserRole;
import com.apiscan.organization.dto.CreateOrgRequest;
import com.apiscan.organization.dto.InviteMemberRequest;
import com.apiscan.organization.dto.OrgResponse;
import com.apiscan.security.RequiresRole;
import com.apiscan.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orgs")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrgResponse>> createOrganization(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateOrgRequest request) {
        OrgResponse response = organizationService.createOrganization(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Organization created", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrgResponse>>> getUserOrganizations(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<OrgResponse> orgs = organizationService.getUserOrganizations(principal.getId());
        return ResponseEntity.ok(ApiResponse.success(orgs));
    }

    @GetMapping("/{orgId}")
    public ResponseEntity<ApiResponse<OrgResponse>> getOrganization(@PathVariable String orgId) {
        OrgResponse response = organizationService.getOrganization(orgId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{orgId}")
    @RequiresRole({ UserRole.OWNER, UserRole.ADMIN })
    public ResponseEntity<ApiResponse<OrgResponse>> updateOrganization(
            @PathVariable String orgId, @RequestBody Map<String, String> request) {
        OrgResponse response = organizationService.updateOrganization(orgId, request.get("name"));
        return ResponseEntity.ok(ApiResponse.success("Organization updated", response));
    }

    @DeleteMapping("/{orgId}")
    @RequiresRole({ UserRole.OWNER })
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(@PathVariable String orgId) {
        organizationService.deleteOrganization(orgId);
        return ResponseEntity.ok(ApiResponse.success("Organization deleted"));
    }

    @PostMapping("/{orgId}/invite")
    @RequiresRole({ UserRole.OWNER, UserRole.ADMIN })
    public ResponseEntity<ApiResponse<Void>> inviteMember(
            @PathVariable String orgId, @Valid @RequestBody InviteMemberRequest request) {
        organizationService.inviteMember(orgId, request);
        return ResponseEntity.ok(ApiResponse.success("Member invited successfully"));
    }

    @GetMapping("/{orgId}/members")
    public ResponseEntity<ApiResponse<List<OrgResponse.MemberInfo>>> getMembers(@PathVariable String orgId) {
        List<OrgResponse.MemberInfo> members = organizationService.getMembers(orgId);
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    @PatchMapping("/{orgId}/members/{userId}")
    @RequiresRole({ UserRole.OWNER })
    public ResponseEntity<ApiResponse<Void>> updateMemberRole(
            @PathVariable String orgId, @PathVariable String userId,
            @RequestBody Map<String, String> request) {
        organizationService.updateMemberRole(orgId, userId, request.get("role"));
        return ResponseEntity.ok(ApiResponse.success("Member role updated"));
    }

    @DeleteMapping("/{orgId}/members/{userId}")
    @RequiresRole({ UserRole.OWNER, UserRole.ADMIN })
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable String orgId, @PathVariable String userId) {
        organizationService.removeMember(orgId, userId);
        return ResponseEntity.ok(ApiResponse.success("Member removed"));
    }
    
    @PostMapping("/invites/{token}/accept")
    public ResponseEntity<ApiResponse<OrgResponse>> acceptInvite(
            @PathVariable String token, 
            @AuthenticationPrincipal UserPrincipal principal) {
        OrgResponse response = organizationService.acceptInvite(token, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Invitation accepted successfully", response));
    }
}
