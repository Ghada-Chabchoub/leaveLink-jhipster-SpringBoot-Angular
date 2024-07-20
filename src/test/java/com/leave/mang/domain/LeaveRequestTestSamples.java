package com.leave.mang.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LeaveRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static LeaveRequest getLeaveRequestSample1() {
        return new LeaveRequest().id(1L).title("title1").description("description1");
    }

    public static LeaveRequest getLeaveRequestSample2() {
        return new LeaveRequest().id(2L).title("title2").description("description2");
    }

    public static LeaveRequest getLeaveRequestRandomSampleGenerator() {
        return new LeaveRequest()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
