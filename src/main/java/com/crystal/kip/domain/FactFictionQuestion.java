package com.crystal.kip.domain;

/** Server-side bank data. Do not serialize this record to the player. */
public record FactFictionQuestion(
    long id,
    long conceptId,
    long gameId,
    long sourceId,
    String prompt,
    long trueAnswerId,
    long falseAnswerId,
    long correctAnswerId,
    String explanation
) {}
