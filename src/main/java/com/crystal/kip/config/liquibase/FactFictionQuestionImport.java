package com.crystal.kip.config.liquibase;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.InsertStatement;
import liquibase.util.csv.CSVReader;

/** Imports the combined question bank into KIP's existing relational tables. */
public class FactFictionQuestionImport implements CustomSqlChange {

    private static final List<String> HEADER = List.of(
        "id",
        "game_id",
        "concept_id",
        "question",
        "correct_answer",
        "explanation",
        "source_id"
    );

    private String csvFile;
    private ResourceAccessor resources;
    private int importedCount;

    // Liquibase calls this setter using the XML parameter named csvFile.
    public void setCsvFile(String csvFile) {
        this.csvFile = csvFile;
    }

    @Override
    public void setFileOpener(ResourceAccessor resources) {
        this.resources = resources;
    }

    @Override
    public void setUp() throws SetupException {
        if (csvFile == null || csvFile.isBlank()) {
            throw new SetupException("csvFile is required");
        }
    }

    @Override
    public ValidationErrors validate(Database database) {
        ValidationErrors errors = new ValidationErrors();
        if (csvFile == null || csvFile.isBlank()) {
            errors.addError("csvFile is required");
        }
        return errors;
    }

    @Override
    public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
        // Parse and validate the complete bank before returning any inserts.
        List<Row> rows = readRows();
        List<SqlStatement> inserts = new ArrayList<>();
        for (Row row : rows) {
            inserts.add(
                insert(database, "question")
                    .addColumnValue("id", row.id())
                    .addColumnValue("question", row.question())
                    .addColumnValue("concept_id", row.conceptId())
            );

            // Feedback IDs reuse question IDs, but belong to a different table.
            inserts.add(
                insert(database, "feedback")
                    .addColumnValue("id", row.id())
                    .addColumnValue("feedback", "Answer explanation")
                    .addColumnValue("explanation", row.explanation())
            );

            inserts.add(answer(database, row, true, row.trueAnswerId()));
            inserts.add(answer(database, row, false, row.trueAnswerId() + 1));

            inserts.add(
                insert(database, "fof_question")
                    .addColumnValue("question_id", row.id())
                    .addColumnValue("game_id", row.gameId())
                    .addColumnValue("source_id", row.sourceId())
            );
        }
        importedCount = rows.size();
        return inserts.toArray(SqlStatement[]::new);
    }

    private InsertStatement answer(Database database, Row row, boolean choice, long answerId) {
        return insert(database, "answer")
            .addColumnValue("id", answerId)
            .addColumnValue("text", choice ? "True" : "False")
            .addColumnValue("correct", choice == row.correctAnswer())
            .addColumnValue("question_id", row.id())
            .addColumnValue("feedback_id", row.id());
        // Narrative next_stage_id, terminal_resolution and outcome_text stay NULL.
    }

    private InsertStatement insert(Database database, String table) {
        return new InsertStatement(database.getDefaultCatalogName(), database.getDefaultSchemaName(), table);
    }

    private List<Row> readRows() throws CustomChangeException {
        List<Row> rows = new ArrayList<>();
        Set<Long> ids = new HashSet<>();
        int record = 1;
        try (
            var input = resources.getExisting(csvFile).openInputStream();
            var reader = new CSVReader(new InputStreamReader(input, StandardCharsets.UTF_8), ';', '"')
        ) {
            String[] header = reader.readNext();
            if (header == null) throw new IllegalArgumentException("The CSV is empty");
            header[0] = header[0].replaceFirst("^\uFEFF", "");
            if (!Arrays.asList(header).equals(HEADER)) {
                throw new IllegalArgumentException("Expected header: " + String.join(";", HEADER));
            }
            String[] fields;
            while ((fields = reader.readNext()) != null) {
                record++;
                if (Arrays.stream(fields).allMatch(value -> value == null || value.isBlank())) continue;
                if (fields.length != HEADER.size()) {
                    throw new IllegalArgumentException("Expected exactly seven columns");
                }
                long id = positiveId(fields[0], "id");
                if (!ids.add(id)) throw new IllegalArgumentException("Duplicate question ID: " + id);
                // Reserve two deterministic answer IDs; reject overflow before any writes.
                long trueAnswerId = Math.multiplyExact(id, 2L);
                Math.addExact(trueAnswerId, 1L);
                String correct = fields[4].trim();
                if (!correct.equalsIgnoreCase("true") && !correct.equalsIgnoreCase("false")) {
                    throw new IllegalArgumentException("correct_answer must be true or false");
                }
                rows.add(
                    new Row(
                        id,
                        positiveId(fields[1], "game_id"),
                        positiveId(fields[2], "concept_id"),
                        text(fields[3], "question"),
                        Boolean.parseBoolean(correct),
                        text(fields[5], "explanation"),
                        positiveId(fields[6], "source_id"),
                        trueAnswerId
                    )
                );
            }
            if (rows.isEmpty()) throw new IllegalArgumentException("The bank has no questions");
            return rows;
        } catch (Exception e) {
            throw new CustomChangeException(csvFile + ", CSV record " + record + ": " + e.getMessage(), e);
        }
    }

    private long positiveId(String value, String field) {
        long id = Long.parseLong(value.trim());
        if (id <= 0) throw new IllegalArgumentException(field + " must be positive");
        return id;
    }

    private String text(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        if (value.codePointCount(0, value.length()) > 255) {
            throw new IllegalArgumentException(field + " exceeds the current 255-character database limit");
        }
        return value;
    }

    @Override
    public String getConfirmationMessage() {
        return (
            "Imported " +
            importedCount +
            " Fact or Fiction questions, explanations and links, and " +
            importedCount * 2 +
            " answer choices from " +
            csvFile
        );
    }

    private record Row(
        long id,
        long gameId,
        long conceptId,
        String question,
        boolean correctAnswer,
        String explanation,
        long sourceId,
        long trueAnswerId
    ) {}
}
