package com.crystal.kip.service.fof;

import com.crystal.kip.repository.fof.FactFictionRepository;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Only the trusted engine calls these methods after checking its active session. */
@Service
@Transactional
public class QuestionHistoryService {

    public record AnswerResult(long questionId, boolean correct, String explanation, boolean duplicate) {}

    private final FactFictionRepository repository;
    private final CurrentPlayerService player;

    public QuestionHistoryService(FactFictionRepository repository, CurrentPlayerService player) {
        this.repository = repository;
        this.player = player;
    }

    public void recordShown(UUID engineSessionId, long questionId, UUID presentationId) {
        Objects.requireNonNull(engineSessionId);
        Objects.requireNonNull(presentationId);
        long user = player.id();
        repository.lockPlayer(user);
        repository.question(questionId);
        var previous = repository.presentation(presentationId);
        if (previous.isPresent()) {
            requireOwner(previous.get(), user, engineSessionId);
            if (previous.get().question() != questionId) throw new IllegalArgumentException("Presentation belongs to another question");
            return; // A repeated display acknowledgement is harmless.
        }
        repository.addPresentation(presentationId, engineSessionId, user, questionId);
    }

    public AnswerResult recordAnswer(UUID engineSessionId, UUID presentationId, long answerId) {
        Objects.requireNonNull(engineSessionId);
        Objects.requireNonNull(presentationId);
        long user = player.id();
        repository.lockPlayer(user);
        var shown = repository.presentation(presentationId).orElseThrow(() -> new IllegalStateException("Question was not presented"));
        requireOwner(shown, user, engineSessionId);
        var question = repository.question(shown.question());
        if (answerId != question.trueAnswerId() && answerId != question.falseAnswerId()) throw new IllegalArgumentException(
            "Answer does not belong to question"
        );
        if (shown.selectedAnswer() != null) {
            if (shown.selectedAnswer() != answerId) throw new IllegalStateException("Cannot change an accepted answer");
            return new AnswerResult(question.id(), shown.correct(), question.explanation(), true);
        }
        boolean correct = answerId == question.correctAnswerId();
        repository.answerPresentation(presentationId, user, question.id(), answerId, correct);
        return new AnswerResult(question.id(), correct, question.explanation(), false);
    }

    private static void requireOwner(FactFictionRepository.Presentation p, long user, UUID session) {
        if (p.player() != user || !p.session().equals(session)) throw new IllegalArgumentException(
            "Presentation does not belong to this player's session"
        );
    }
}
