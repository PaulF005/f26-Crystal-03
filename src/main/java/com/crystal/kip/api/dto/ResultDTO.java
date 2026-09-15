package com.crystal.kip.api.dto;

public record ResultDTO(
    String feedbackText, // Note that this cannot be a static property of a question, as the feedback must be evaluated by the Feedback System
    String outcomeText   // Outcome text, however, can safely be static, because it is simply the description of what the result of the answer is
) {}
