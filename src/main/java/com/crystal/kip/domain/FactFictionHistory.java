package com.crystal.kip.domain;

public record FactFictionHistory(int shown, int correct, int incorrect) {
    public FactFictionHistory {
        if (shown < 0 || correct < 0 || incorrect < 0 || (long) correct + incorrect > shown) {
            throw new IllegalArgumentException("Invalid history counts");
        }
    }
}
