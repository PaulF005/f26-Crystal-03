package com.crystal.kip.game;

import com.crystal.kip.domain.Scenario;
import com.crystal.kip.domain.enumeration.ScenarioResolution;

public class ScenarioResult {

    private final Scenario scenario;
    private final ScenarioResolution resolution;

    public ScenarioResult(Scenario scenario, ScenarioResolution resolution) {
        this.scenario = scenario;
        this.resolution = resolution;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public ScenarioResolution getResolution() {
        return resolution;
    }
}
