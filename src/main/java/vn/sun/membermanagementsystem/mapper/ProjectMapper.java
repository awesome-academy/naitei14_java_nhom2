package vn.sun.membermanagementsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vn.sun.membermanagementsystem.dto.response.ProjectDTO;
import vn.sun.membermanagementsystem.entities.Project;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    
    @Mapping(source = "team.id", target = "teamId")
    @Mapping(source = "team.name", target = "teamName")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")

    ProjectDTO toDTO(Project project);

    List<ProjectDTO> toDTOList(List<Project> projects);

    @Named("statusToString")
    default String statusToString(Project.ProjectStatus status) {
        return status != null ? status.name() : null;
    }
}
