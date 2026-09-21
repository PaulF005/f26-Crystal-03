package com.crystal.kip.api.dto;

import java.util.List;

public record StageDTO(Long stageId, String prompt, List<AnswerDTO> answers) {}
