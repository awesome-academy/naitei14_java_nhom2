package vn.sun.membermanagementsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import vn.sun.membermanagementsystem.dto.response.*;
import vn.sun.membermanagementsystem.entities.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserSummaryDTO toSummaryDTO(User user);

    List<UserSummaryDTO> toSummaryDTOList(List<User> users);
}
