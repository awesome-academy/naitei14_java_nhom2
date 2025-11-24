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
public class UserPositionHistoryDTO {
    private Long id;
    private Long positionId;
    private String positionName;
    private String positionAbbreviation;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Boolean isActive;
}
