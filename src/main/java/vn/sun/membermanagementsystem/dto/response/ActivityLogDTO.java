package vn.sun.membermanagementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.sun.membermanagementsystem.entities.ActivityLog;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDTO {
    private Long id;
    private String action;
    private String entityType;
    private Long entityId;
    private String description;
    private Long userId;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
}
