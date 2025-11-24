package vn.sun.membermanagementsystem.mapper;

import org.mapstruct.Mapper;
import vn.sun.membermanagementsystem.dto.response.ActivityLogDTO;
import vn.sun.membermanagementsystem.entities.ActivityLog;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper {

    ActivityLogDTO toDTO(ActivityLog activityLog);

    List<ActivityLogDTO> toDTOList(List<ActivityLog> activityLogs);

    ActivityLog toEntity(ActivityLogDTO activityLogDTO);
}
