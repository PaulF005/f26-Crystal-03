package com.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserDetailTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static UserDetail getUserDetailSample1() {
        return new UserDetail().id(1L).username("username1").email("email1");
    }

    public static UserDetail getUserDetailSample2() {
        return new UserDetail().id(2L).username("username2").email("email2");
    }

    public static UserDetail getUserDetailRandomSampleGenerator() {
        return new UserDetail().id(longCount.incrementAndGet()).username(UUID.randomUUID().toString()).email(UUID.randomUUID().toString());
    }
}
