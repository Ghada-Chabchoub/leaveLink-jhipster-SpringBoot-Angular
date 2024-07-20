package com.leave.mang.domain;

import static com.leave.mang.domain.LeaveRequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.leave.mang.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LeaveRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LeaveRequest.class);
        LeaveRequest leaveRequest1 = getLeaveRequestSample1();
        LeaveRequest leaveRequest2 = new LeaveRequest();
        assertThat(leaveRequest1).isNotEqualTo(leaveRequest2);

        leaveRequest2.setId(leaveRequest1.getId());
        assertThat(leaveRequest1).isEqualTo(leaveRequest2);

        leaveRequest2 = getLeaveRequestSample2();
        assertThat(leaveRequest1).isNotEqualTo(leaveRequest2);
    }
}
