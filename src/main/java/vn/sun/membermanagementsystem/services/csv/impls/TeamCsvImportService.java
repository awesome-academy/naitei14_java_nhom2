package vn.sun.membermanagementsystem.services.csv.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.sun.membermanagementsystem.dto.request.csv.CsvImportResult;
import vn.sun.membermanagementsystem.entities.Team;
import vn.sun.membermanagementsystem.entities.User;
import vn.sun.membermanagementsystem.repositories.TeamRepository;
import vn.sun.membermanagementsystem.repositories.UserRepository;
import vn.sun.membermanagementsystem.services.TeamLeadershipService;
import vn.sun.membermanagementsystem.services.TeamService;
import vn.sun.membermanagementsystem.services.csv.AbstractCsvImportService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamCsvImportService extends AbstractCsvImportService<Team> {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamService teamService;
    private final TeamLeadershipService teamLeadershipService;

    // Column indices
    private static final int COL_NAME = 0;
    private static final int COL_DESCRIPTION = 1;
    private static final int COL_LEADER_EMAIL = 2;

    @Override
    protected List<String> validateRowForPreview(String[] data, int rowNumber) {
        return validateRowData(data);
    }

    private List<String> validateRowData(String[] data) {
        List<String> errors = new ArrayList<>();

        // Validate name (required)
        String name = getStringValue(data, COL_NAME);
        if (isBlank(name)) {
            errors.add("Name is required");
        } else if (name.length() > 255) {
            errors.add("Name must be less than 255 characters");
        } else if (teamRepository.existsByNameAndNotDeleted(name)) {
            errors.add("Team name already exists: " + name);
        }

        // Validate leader email (optional)
        String leaderEmail = getStringValue(data, COL_LEADER_EMAIL);
        if (isNotBlank(leaderEmail)) {
            if (!userRepository.existsByEmailAndNotDeleted(leaderEmail)) {
                errors.add("Leader email not found: " + leaderEmail);
            }
        }

        // Description is optional, no validation needed

        return errors;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CsvImportResult<Team> importFromCsv(MultipartFile file) {
        return super.importFromCsv(file);
    }

    @Override
    protected Team processRow(String[] data, int rowNumber, CsvImportResult<Team> result) {
        String name = getStringValue(data, COL_NAME);
        String description = getStringValue(data, COL_DESCRIPTION);
        String leaderEmail = getStringValue(data, COL_LEADER_EMAIL);

        // Create new Team entity
        Team team = new Team();
        team.setName(name.trim());
        team.setDescription(isNotBlank(description) ? description.trim() : null);
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());

        // Save to database
        Team savedTeam = teamRepository.save(team);
        log.info("Row {}: Created team '{}' with ID: {}",
                rowNumber, savedTeam.getName(), savedTeam.getId());

        // Assign leader if email provided
        if (isNotBlank(leaderEmail)) {
            try {
                User leader = userRepository.findByEmailAndNotDeleted(leaderEmail)
                        .orElseThrow(() -> new RuntimeException("Leader not found: " + leaderEmail));

                // Add leader as member first
                teamService.addMemberToTeam(savedTeam.getId(), leader.getId());
                log.info("Row {}: Added leader as member to team '{}'", rowNumber, savedTeam.getName());

                // Assign as leader
                teamLeadershipService.assignLeader(savedTeam.getId(), leader.getId());
                log.info("Row {}: Assigned leader '{}' to team '{}'",
                        rowNumber, leader.getEmail(), savedTeam.getName());

            } catch (Exception e) {
                log.warn("Row {}: Failed to assign leader '{}' to team '{}': {}",
                        rowNumber, leaderEmail, savedTeam.getName(), e.getMessage());
                result.addError(rowNumber, "Leader Assignment",
                        "Failed to assign leader: " + e.getMessage());
            }
        }

        return savedTeam;
    }

    @Override
    public boolean validateRow(String[] data, int rowNumber, CsvImportResult<Team> result) {
        List<String> errors = validateRowData(data);

        for (String error : errors) {
            result.addError(rowNumber, "Validation", error);
        }

        return errors.isEmpty();
    }

    @Override
    public String[] getExpectedHeaders() {
        return new String[] {
                "Name",
                "Description",
                "Leader Email"
        };
    }

    @Override
    public String generateSampleCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", getExpectedHeaders())).append("\n");
        sb.append("Frontend Team,Team handling frontend development,john@example.com\n");
        sb.append("Backend Team,Team handling backend services,jane@example.com\n");
        sb.append("DevOps Team,Team managing infrastructure and deployment,\n");
        sb.append("Mobile Team,Team developing mobile applications,mike@example.com\n");
        return sb.toString();
    }
}
