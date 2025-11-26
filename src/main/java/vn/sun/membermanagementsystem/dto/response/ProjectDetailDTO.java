package vn.sun.membermanagementsystem.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectDetailDTO extends ProjectDTO {
    private List<ProjectMemberDTO> members = new ArrayList<>();
    private List<ProjectLeadershipHistoryDTO> leadershipHistory = new ArrayList<>();
}