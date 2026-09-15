package com.crystal.kip.api.dto;

import java.util.UUID;

public record SessionDTO(
    UUID id,
    String gameTitle,
    ScenarioDTO currentScenario
) {}