package vn.sun.membermanagementsystem.services.csv.impls;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.sun.membermanagementsystem.entities.Team;
import vn.sun.membermanagementsystem.repositories.TeamRepository;
import vn.sun.membermanagementsystem.services.csv.CsvExportService;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamCsvExportService implements CsvExportService<Team> {

    private final TeamRepository teamRepository;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional(readOnly = true)
    public void exportToCsv(OutputStream outputStream) throws IOException {
        log.info("Starting export of teams to CSV");

        List<Team> teams = teamRepository.findAllNotDeleted();

        try (CSVWriter writer = new CSVWriter(
                new OutputStreamWriter(outputStream, StandardCharsets.UTF_8),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {

            // Write BOM for Excel UTF-8 support
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);

            writer.writeNext(getExportHeaders());

            for (Team team : teams) {
                String[] row = convertTeamToRow(team);
                writer.writeNext(row);
            }

            log.info("Successfully exported {} teams to CSV", teams.size());
        }
    }

    private String[] convertTeamToRow(Team team) {
        List<String> row = new ArrayList<>();

        // ID
        row.add(team.getId() != null ? team.getId().toString() : "");

        // Name
        row.add(team.getName() != null ? team.getName() : "");

        // Description
        row.add(team.getDescription() != null ? team.getDescription() : "");

        // Current Leader Name and Email
        String leaderName = "";
        String leaderEmail = "";
        if (team.getLeadershipHistory() != null) {
            var currentLeader = team.getLeadershipHistory().stream()
                    .filter(lh -> lh.getEndedAt() == null)
                    .findFirst();
            if (currentLeader.isPresent()) {
                leaderName = currentLeader.get().getLeader().getName() != null 
                        ? currentLeader.get().getLeader().getName() : "";
                leaderEmail = currentLeader.get().getLeader().getEmail() != null 
                        ? currentLeader.get().getLeader().getEmail() : "";
            }
        }
        row.add(leaderName);
        row.add(leaderEmail);

        // Active Members Count
        long activeMembersCount = 0;
        if (team.getTeamMemberships() != null) {
            activeMembersCount = team.getTeamMemberships().stream()
                    .filter(tm -> tm.getStatus() != null 
                            && tm.getStatus().name().equals("ACTIVE") 
                            && tm.getLeftAt() == null)
                    .count();
        }
        row.add(String.valueOf(activeMembersCount));

        // Total Members Count (not left)
        long totalMembersCount = 0;
        if (team.getTeamMemberships() != null) {
            totalMembersCount = team.getTeamMemberships().stream()
                    .filter(tm -> tm.getLeftAt() == null)
                    .count();
        }
        row.add(String.valueOf(totalMembersCount));

        // Active Projects Count
        long activeProjectsCount = 0;
        if (team.getProjects() != null) {
            activeProjectsCount = team.getProjects().stream()
                    .filter(p -> p.getDeletedAt() == null 
                            && p.getStatus() != null
                            && !p.getStatus().name().equals("COMPLETED")
                            && !p.getStatus().name().equals("CANCELLED"))
                    .count();
        }
        row.add(String.valueOf(activeProjectsCount));

        // Total Projects Count
        long totalProjectsCount = 0;
        if (team.getProjects() != null) {
            totalProjectsCount = team.getProjects().stream()
                    .filter(p -> p.getDeletedAt() == null)
                    .count();
        }
        row.add(String.valueOf(totalProjectsCount));

        // Created At
        row.add(team.getCreatedAt() != null ? team.getCreatedAt().format(DATETIME_FORMATTER) : "");

        // Updated At
        row.add(team.getUpdatedAt() != null ? team.getUpdatedAt().format(DATETIME_FORMATTER) : "");

        return row.toArray(new String[0]);
    }

    @Override
    public String[] getExportHeaders() {
        return new String[]{
                "ID",
                "Name",
                "Description",
                "Current Leader",
                "Leader Email",
                "Active Members",
                "Total Members",
                "Active Projects",
                "Total Projects",
                "Created At",
                "Updated At"
        };
    }
}
