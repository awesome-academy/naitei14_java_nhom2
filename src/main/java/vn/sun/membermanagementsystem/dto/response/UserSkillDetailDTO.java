package vn.sun.membermanagementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSkillDetailDTO {
    private Long id;
    private Long skillId;
    private String skillName;
    private String skillDescription;
    private Integer skillLevel;
    private Double yearsOfExperience;
}
