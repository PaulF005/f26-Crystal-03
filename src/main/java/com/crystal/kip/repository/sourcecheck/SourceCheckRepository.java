package com.crystal.kip.repository.sourcecheck;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SourceCheckRepository {

    public record SourceUrl(long sourceId, String url) {}

    private final JdbcTemplate jdbc;

    public SourceCheckRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<SourceUrl> sources() {
        return jdbc.query("SELECT id,url FROM source WHERE url IS NOT NULL AND TRIM(url)<>'' ORDER BY id", (row, number) ->
            new SourceUrl(row.getLong("id"), row.getString("url"))
        );
    }

    public List<Long> affectedQuestions(long sourceId) {
        return jdbc.query(
            "SELECT question_id FROM fof_question WHERE source_id=? ORDER BY question_id",
            (row, number) -> row.getLong(1),
            sourceId
        );
    }
}
