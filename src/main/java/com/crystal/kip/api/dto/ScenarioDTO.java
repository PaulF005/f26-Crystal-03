package com.crystal.kip.api.dto;

import java.util.UUID;

public record ScenarioDTO(
    Long scenarioId,
    StageDTO currentStage,
    int stageNumber,
    int stageCount
) {}
