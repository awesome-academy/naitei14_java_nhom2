package vn.sun.membermanagementsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vn.sun.membermanagementsystem.dto.response.TeamDTO;
import vn.sun.membermanagementsystem.entities.Team;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    
    TeamDTO toDTO(Team team);
    
    List<TeamDTO> toDTOList(List<Team> teams);
    
    Team toEntity(TeamDTO teamDTO);
}
