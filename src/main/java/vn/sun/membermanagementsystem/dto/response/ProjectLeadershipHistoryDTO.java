package vn.sun.membermanagementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectLeadershipHistoryDTO {
    private Long id;
    private String leaderName;
    private String leaderEmail;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private boolean isCurrent;
}