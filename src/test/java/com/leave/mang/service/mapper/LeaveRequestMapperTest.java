package com.leave.mang.service.mapper;

import static com.leave.mang.domain.LeaveRequestAsserts.*;
import static com.leave.mang.domain.LeaveRequestTestSamples.*;

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
