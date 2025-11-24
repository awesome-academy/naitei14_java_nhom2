package vn.sun.membermanagementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private UserSummaryDTO userInfo;
    private List<UserProjectParticipationDTO> projectParticipations;
    private List<UserPositionHistoryDTO> positionHistory;
    private List<UserSkillDetailDTO> skills;
    private List<UserTeamMembershipDTO> teamMemberships;
}
