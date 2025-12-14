package vn.sun.membermanagementsystem.services.csv.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.sun.membermanagementsystem.dto.request.CreateTeamRequest;
import vn.sun.membermanagementsystem.dto.request.csv.CsvImportResult;
import vn.sun.membermanagementsystem.dto.response.TeamDTO;
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

        // Find leader if email provided
        Long leaderId = null;
        if (isNotBlank(leaderEmail)) {
            try {
                User leader = userRepository.findByEmailAndNotDeleted(leaderEmail)
                        .orElseThrow(() -> new RuntimeException("Leader not found: " + leaderEmail));
                leaderId = leader.getId();
            } catch (Exception e) {
                log.warn("Row {}: Leader not found '{}': {}",
                        rowNumber, leaderEmail, e.getMessage());
            }
        }

        // Create team using service (this will log CREATE_TEAM)
        CreateTeamRequest request = new CreateTeamRequest();
        request.setName(name.trim());
        request.setDescription(isNotBlank(description) ? description.trim() : null);
        request.setLeaderId(leaderId);

        TeamDTO teamDTO = teamService.createTeam(request);
        log.info("Row {}: Created team '{}' with ID: {}",
                rowNumber, teamDTO.getName(), teamDTO.getId());

        // Return the Team entity (need to fetch from repository)
        Team savedTeam = teamRepository.findById(teamDTO.getId())
                .orElseThrow(() -> new RuntimeException("Team not found after creation"));

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
        sb.append("\"Frontend Team\",\"Team handling frontend development\",\"john@example.com\"\n");
        sb.append("\"Backend Team\",\"Team handling backend services\",\"jane@example.com\"\n");
        sb.append("\"DevOps Team\",\"Team managing infrastructure and deployment\",\"\"\n");
        sb.append("\"Mobile Team\",\"Team developing mobile applications\",\"mike@example.com\"\n");
        return sb.toString();
    }
}
