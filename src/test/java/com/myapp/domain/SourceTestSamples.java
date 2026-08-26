package com.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SourceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Source getSourceSample1() {
        return new Source().id(1L).name("name1").url("url1").date("date1").data("data1");
    }

    public static Source getSourceSample2() {
        return new Source().id(2L).name("name2").url("url2").date("date2").data("data2");
    }

    public static Source getSourceRandomSampleGenerator() {
        return new Source()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .url(UUID.randomUUID().toString())
            .date(UUID.randomUUID().toString())
            .data(UUID.randomUUID().toString());
    }
}
