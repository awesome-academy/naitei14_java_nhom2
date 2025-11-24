package vn.sun.membermanagementsystem.mapper;

import org.mapstruct.Mapper;
import vn.sun.membermanagementsystem.dto.response.SkillDTO;
import vn.sun.membermanagementsystem.entities.Skill;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    
    SkillDTO toDTO(Skill skill);
    
    List<SkillDTO> toDTOList(List<Skill> skills);
    
    Skill toEntity(SkillDTO skillDTO);
}
