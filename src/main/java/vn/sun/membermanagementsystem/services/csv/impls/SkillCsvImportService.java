package vn.sun.membermanagementsystem.services.csv.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.sun.membermanagementsystem.dto.request.csv.CsvImportResult;
import vn.sun.membermanagementsystem.entities.Skill;
import vn.sun.membermanagementsystem.repositories.SkillRepository;
import vn.sun.membermanagementsystem.services.csv.AbstractCsvImportService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillCsvImportService extends AbstractCsvImportService<Skill> {

    private final SkillRepository skillRepository;

    // Column indices
    private static final int COL_NAME = 0;
    private static final int COL_DESCRIPTION = 1;

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
        } else if (skillRepository.existsByNameIgnoreCaseAndNotDeleted(name)) {
            errors.add("Skill name already exists: " + name);
        }

        // Description is optional, no validation needed

        return errors;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CsvImportResult<Skill> importFromCsv(MultipartFile file) {
        return super.importFromCsv(file);
    }

    @Override
    protected Skill processRow(String[] data, int rowNumber, CsvImportResult<Skill> result) {
        String name = getStringValue(data, COL_NAME);
        String description = getStringValue(data, COL_DESCRIPTION);

        // Create new Skill entity
        Skill skill = new Skill();
        skill.setName(name.trim());
        skill.setDescription(isNotBlank(description) ? description.trim() : null);
        skill.setCreatedAt(LocalDateTime.now());
        skill.setUpdatedAt(LocalDateTime.now());

        // Save to database
        Skill savedSkill = skillRepository.save(skill);
        log.info("Row {}: Created skill '{}' with ID: {}", 
                rowNumber, savedSkill.getName(), savedSkill.getId());

        return savedSkill;
    }

    @Override
    public boolean validateRow(String[] data, int rowNumber, CsvImportResult<Skill> result) {
        List<String> errors = validateRowData(data);

        for (String error : errors) {
            result.addError(rowNumber, "Validation", error);
        }

        return errors.isEmpty();
    }

    @Override
    public String[] getExpectedHeaders() {
        return new String[]{
                "Name",
                "Description"
        };
    }

    @Override
    public String generateSampleCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", getExpectedHeaders())).append("\n");
        sb.append("Java,Programming language for enterprise applications\n");
        sb.append("Python,Versatile programming language for data science and web\n");
        sb.append("React,JavaScript library for building user interfaces\n");
        sb.append("Spring Boot,Java framework for microservices\n");
        return sb.toString();
    }
}
