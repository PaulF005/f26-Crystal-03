package com.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LegalContentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static LegalContent getLegalContentSample1() {
        return new LegalContent().id(1L).name("name1");
    }

    public static LegalContent getLegalContentSample2() {
        return new LegalContent().id(2L).name("name2");
    }

    public static LegalContent getLegalContentRandomSampleGenerator() {
        return new LegalContent().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
