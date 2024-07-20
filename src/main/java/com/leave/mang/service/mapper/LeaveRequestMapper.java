package com.leave.mang.service.mapper;

import com.leave.mang.domain.LeaveRequest;
import com.leave.mang.domain.User;
import com.leave.mang.service.dto.LeaveRequestDTO;
import com.leave.mang.service.dto.UserDTO;
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
