package com.crystal.kip.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ConceptTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Concept getConceptSample1() {
        return new Concept().id(1L).name("name1").explanation("explanation1");
    }

    public static Concept getConceptSample2() {
        return new Concept().id(2L).name("name2").explanation("explanation2");
    }

    public static Concept getConceptRandomSampleGenerator() {
        return new Concept().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).explanation(UUID.randomUUID().toString());
    }
}
