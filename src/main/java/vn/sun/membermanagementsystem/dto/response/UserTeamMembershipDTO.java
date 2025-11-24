package vn.sun.membermanagementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.sun.membermanagementsystem.enums.MembershipStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTeamMembershipDTO {
    private Long id;
    private Long teamId;
    private String teamName;
    private String teamDescription;
    private MembershipStatus status;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private Boolean isActive;
}
