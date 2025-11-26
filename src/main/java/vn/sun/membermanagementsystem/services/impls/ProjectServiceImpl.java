package vn.sun.membermanagementsystem.services.impls;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.sun.membermanagementsystem.dto.response.ProjectDTO;
import vn.sun.membermanagementsystem.dto.response.ProjectDetailDTO;
import vn.sun.membermanagementsystem.dto.response.ProjectLeadershipHistoryDTO;
import vn.sun.membermanagementsystem.dto.response.ProjectMemberDTO;
import vn.sun.membermanagementsystem.entities.Project;
import vn.sun.membermanagementsystem.entities.Team;
import vn.sun.membermanagementsystem.mapper.ProjectMapper;
import vn.sun.membermanagementsystem.repositories.ProjectRepository;
import vn.sun.membermanagementsystem.services.ProjectService;
import vn.sun.membermanagementsystem.services.TeamService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;
    private final ProjectMapper projectMapper;
    private final TeamService teamService;

    @Override
    public Page<ProjectDTO> getAllProjects(Long teamId, Pageable pageable) {
        Page<Project> projectsPage;

        if (teamId != null) {
            Team team = teamService.getRequiredTeam(teamId);

            projectsPage = projectRepo.findByTeam(team, pageable);
        } else {
            projectsPage = projectRepo.findAll(pageable);
        }

        return projectsPage.map(projectMapper::toDTO);
    }

    @Override
    public ProjectDetailDTO getProjectDetail(Long id) {
        Project project = projectRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));

        ProjectDTO basicDto = projectMapper.toDTO(project);
        ProjectDetailDTO detailDTO = new ProjectDetailDTO();

        detailDTO.setId(basicDto.getId());
        detailDTO.setName(basicDto.getName());
        detailDTO.setAbbreviation(basicDto.getAbbreviation());
        detailDTO.setStartDate(basicDto.getStartDate());
        detailDTO.setEndDate(basicDto.getEndDate());
        detailDTO.setStatus(basicDto.getStatus());
        detailDTO.setTeamId(basicDto.getTeamId());
        detailDTO.setTeamName(basicDto.getTeamName());
        detailDTO.setCreatedAt(basicDto.getCreatedAt());
        detailDTO.setUpdatedAt(basicDto.getUpdatedAt());

        List<ProjectMemberDTO> memberDTOs = project.getProjectMembers().stream()
                .sorted(Comparator.comparing(vn.sun.membermanagementsystem.entities.ProjectMember::getJoinedAt).reversed())
                .map(pm -> ProjectMemberDTO.builder()
                        .id(pm.getId())
                        .userId(pm.getUser().getId())
                        .userName(pm.getUser().getName())
                        .userEmail(pm.getUser().getEmail())
                        .status(pm.getStatus().name())
                        .joinedAt(pm.getJoinedAt())
                        .leftAt(pm.getLeftAt())
                        .build())
                .collect(Collectors.toList());
        detailDTO.setMembers(memberDTOs);

        List<ProjectLeadershipHistoryDTO> historyDTOs = project.getLeadershipHistory().stream()
                .sorted(Comparator.comparing(vn.sun.membermanagementsystem.entities.ProjectLeadershipHistory::getStartedAt).reversed())
                .map(hist -> ProjectLeadershipHistoryDTO.builder()
                        .id(hist.getId())
                        .leaderName(hist.getLeader().getName())
                        .leaderEmail(hist.getLeader().getEmail())
                        .startedAt(hist.getStartedAt())
                        .endedAt(hist.getEndedAt())
                        .isCurrent(hist.getEndedAt() == null)
                        .build())
                .collect(Collectors.toList());
        detailDTO.setLeadershipHistory(historyDTOs);

        return detailDTO;
    }
}