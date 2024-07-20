package com.leave.link.service.mapper;

import static com.leave.link.domain.LeaveRequestAsserts.*;
import static com.leave.link.domain.LeaveRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LeaveRequestMapperTest {

    private LeaveRequestMapper leaveRequestMapper;

    @BeforeEach
    void setUp() {
        leaveRequestMapper = new LeaveRequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLeaveRequestSample1();
        var actual = leaveRequestMapper.toEntity(leaveRequestMapper.toDto(expected));
        assertLeaveRequestAllPropertiesEquals(expected, actual);
    }
}
