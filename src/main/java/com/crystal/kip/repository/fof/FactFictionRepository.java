package com.crystal.kip.repository.fof;

import com.crystal.kip.domain.FactFictionHistory;
import com.crystal.kip.domain.FactFictionQuestion;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** SQL adapter for existing content plus small support tables; no generated entities are replaced. */
@Repository
public class FactFictionRepository {

    private final JdbcTemplate jdbc;

    public FactFictionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String BANK = """
    SELECT q.id,q.concept_id,m.game_id,m.source_id,q.question,
           t.id AS true_id,z.id AS false_id,
           CASE WHEN t.correct THEN t.id ELSE z.id END AS correct_id,f.explanation
    FROM fof_question m JOIN question q ON q.id=m.question_id
    JOIN concept c ON c.id=q.concept_id
    JOIN answer t ON t.question_id=q.id AND t.text='True'
    JOIN answer z ON z.question_id=q.id AND z.text='False'
    JOIN feedback f ON f.id=t.feedback_id AND f.id=z.feedback_id
    WHERE t.correct<>z.correct AND t.next_stage_id IS NULL AND z.next_stage_id IS NULL
      AND t.terminal_resolution IS NULL AND z.terminal_resolution IS NULL
      AND (SELECT COUNT(*) FROM answer a WHERE a.question_id=q.id)=2
    """;

    private List<FactFictionQuestion> queryBank(String condition, Object... params) {
        return jdbc.query(
            BANK + condition + " ORDER BY q.id",
            (r, n) -> {
                if (r.getString("explanation") == null || r.getString("explanation").isBlank()) {
                    throw new IllegalStateException("Missing explanation for question " + r.getLong("id"));
                }
                return new FactFictionQuestion(
                    r.getLong("id"),
                    r.getLong("concept_id"),
                    r.getLong("game_id"),
                    r.getLong("source_id"),
                    r.getString("question"),
                    r.getLong("true_id"),
                    r.getLong("false_id"),
                    r.getLong("correct_id"),
                    r.getString("explanation")
                );
            },
            params
        );
    }

    public List<FactFictionQuestion> bank(long game, long topic, long concept) {
        List<FactFictionQuestion> result = queryBank(" AND m.game_id=? AND c.topic_id=? AND c.id=?", game, topic, concept);
        Integer expected = jdbc.queryForObject(
            """
            SELECT COUNT(*) FROM fof_question m JOIN question q ON q.id=m.question_id
            JOIN concept c ON c.id=q.concept_id WHERE m.game_id=? AND c.topic_id=? AND c.id=?
            """,
            Integer.class,
            game,
            topic,
            concept
        );
        if (expected != result.size()) throw new IllegalStateException("Malformed True/False bank; check answers and feedback links");
        return result;
    }

    public FactFictionQuestion question(long id) {
        List<FactFictionQuestion> result = queryBank(" AND q.id=?", id);
        if (result.size() != 1) throw new IllegalArgumentException("Valid Fact or Fiction question not found: " + id);
        return result.getFirst();
    }

    public long playerId(String login) {
        List<Long> ids = jdbc.query(
            "SELECT p.id FROM user_profile p JOIN jhi_user u ON u.id=p.data_user_id WHERE u.login=?",
            (r, n) -> r.getLong(1),
            login
        );
        if (ids.size() != 1) throw new IllegalStateException("Current user needs exactly one player profile");
        return ids.getFirst();
    }

    public void lockPlayer(long player) {
        jdbc.queryForObject("SELECT id FROM user_profile WHERE id=? FOR UPDATE", Long.class, player);
    }

    public Map<Long, FactFictionHistory> history(long player) {
        Map<Long, FactFictionHistory> result = new HashMap<>();
        jdbc.query(
            "SELECT question_id,times_shown,times_correct,times_incorrect FROM question_history WHERE user_profile_id=?",
            (org.springframework.jdbc.core.RowCallbackHandler) r ->
                result.put(r.getLong(1), new FactFictionHistory(r.getInt(2), r.getInt(3), r.getInt(4))),
            player
        );
        return result;
    }

    public record Presentation(UUID id, UUID session, long player, long question, Long selectedAnswer, Boolean correct) {}

    public Optional<Presentation> presentation(UUID id) {
        return jdbc
            .query(
                "SELECT * FROM fof_presentation WHERE id=?",
                (r, n) ->
                    new Presentation(
                        UUID.fromString(r.getString("id")),
                        UUID.fromString(r.getString("engine_session_id")),
                        r.getLong("user_profile_id"),
                        r.getLong("question_id"),
                        r.getObject("selected_answer_id", Long.class),
                        r.getObject("correct", Boolean.class)
                    ),
                id.toString()
            )
            .stream()
            .findFirst();
    }

    public void addPresentation(UUID id, UUID session, long player, long question) {
        jdbc.update(
            "INSERT INTO fof_presentation(id,engine_session_id,user_profile_id,question_id,shown_at) VALUES (?,?,?,?,?)",
            id.toString(),
            session.toString(),
            player,
            question,
            Timestamp.from(Instant.now())
        );
        int updated = jdbc.update(
            "UPDATE question_history SET times_shown=times_shown+1 WHERE user_profile_id=? AND question_id=?",
            player,
            question
        );
        if (updated == 0) jdbc.update(
            "INSERT INTO question_history(user_profile_id,question_id,times_shown,times_correct,times_incorrect) VALUES (?,?,1,0,0)",
            player,
            question
        );
    }

    public void answerPresentation(UUID id, long player, long question, long answer, boolean correct) {
        int updated = jdbc.update(
            "UPDATE fof_presentation SET selected_answer_id=?,correct=?,answered_at=? WHERE id=? AND selected_answer_id IS NULL",
            answer,
            correct,
            Timestamp.from(Instant.now()),
            id.toString()
        );
        if (updated != 1) throw new IllegalStateException("Presentation is already answered");
        String column = correct ? "times_correct" : "times_incorrect";
        updated = jdbc.update(
            "UPDATE question_history SET " + column + "=" + column + "+1 WHERE user_profile_id=? AND question_id=?",
            player,
            question
        );
        if (updated != 1) throw new IllegalStateException("Missing question history");
    }
}
