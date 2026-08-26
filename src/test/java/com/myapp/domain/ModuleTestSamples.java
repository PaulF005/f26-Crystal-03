package com.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ModuleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Module getModuleSample1() {
        return new Module().id(1L).name("name1");
    }

    public static Module getModuleSample2() {
        return new Module().id(2L).name("name2");
    }

    public static Module getModuleRandomSampleGenerator() {
        return new Module().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
