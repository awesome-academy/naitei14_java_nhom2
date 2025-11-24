package vn.sun.membermanagementsystem.mapper;

import org.mapstruct.Mapper;
import vn.sun.membermanagementsystem.dto.response.PositionDTO;
import vn.sun.membermanagementsystem.entities.Position;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PositionMapper {
    
    PositionDTO toDTO(Position position);

    List<PositionDTO> toDTOList(List<Position> positions);

    Position toEntity(PositionDTO positionDTO);
}
