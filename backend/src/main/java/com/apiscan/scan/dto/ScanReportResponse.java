
package com.apiscan.scan.dto;

import java.util.*;
import lombok.Data;
import com.apiscan.scan.dto.VulnerabilityResponse;
@Data
public class ScanReportResponse {
    private String scanId;
    private String status;
    private long duration;
    private Map<String, Object> summary;
    private List<VulnerabilityResponse> vulnerabilities;
}
