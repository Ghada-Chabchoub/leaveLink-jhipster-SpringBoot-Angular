package com.leave.link.service.mapper;

import com.leave.link.domain.LeaveRequest;
import com.leave.link.domain.User;
import com.leave.link.service.dto.LeaveRequestDTO;
import com.leave.link.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LeaveRequest} and its DTO {@link LeaveRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface LeaveRequestMapper extends EntityMapper<LeaveRequestDTO, LeaveRequest> {
    @Mapping(target = "employee", source = "employee", qualifiedByName = "userLogin")
    LeaveRequestDTO toDto(LeaveRequest s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
