package com.crystal.kip.config.liquibase;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FactFictionQuestionImportTest {

    @TempDir
    Path root;

    Connection connection;
    Path csv;
    static final String HEADER = "id;game_id;concept_id;question;correct_answer;explanation;source_id\n";
    static final String BANK =
        HEADER +
        "20101;10;201;A sample true statement.;true;\"An explanation; with a semicolon.\";4\n" +
        "20102;10;201;A sample false statement.;false;\"It says \"\"False\"\".\";4\n";

    @BeforeEach
    void setUp() throws Exception {
        connection = DriverManager.getConnection("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL");
        try (var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE game(id BIGINT PRIMARY KEY)");
            statement.execute("CREATE TABLE concept(id BIGINT PRIMARY KEY)");
            statement.execute("CREATE TABLE source(id BIGINT PRIMARY KEY)");
            statement.execute("INSERT INTO game VALUES(10)");
            statement.execute("INSERT INTO concept VALUES(201),(202),(203)");
            statement.execute("INSERT INTO source VALUES(4)");
            statement.execute(
                "CREATE TABLE question(id BIGINT PRIMARY KEY, question VARCHAR(255) NOT NULL, concept_id BIGINT NOT NULL REFERENCES concept(id))"
            );
            statement.execute("CREATE TABLE feedback(id BIGINT PRIMARY KEY, feedback VARCHAR(255) NOT NULL, explanation VARCHAR(255))");
            statement.execute(
                "CREATE TABLE answer(id BIGINT PRIMARY KEY, text VARCHAR(255) NOT NULL, correct BOOLEAN NOT NULL, question_id BIGINT REFERENCES question(id), feedback_id BIGINT REFERENCES feedback(id), next_stage_id BIGINT, terminal_resolution VARCHAR(255), outcome_text VARCHAR(255))"
            );
            statement.execute(
                "CREATE TABLE fof_question(question_id BIGINT PRIMARY KEY REFERENCES question(id), game_id BIGINT NOT NULL REFERENCES game(id), source_id BIGINT NOT NULL REFERENCES source(id))"
            );
        }
        Path changelog = root.resolve("import.xml");
        try (var stream = getClass().getResourceAsStream("/config/liquibase/changelog/20261102000400_load_FofQuestions.xml")) {
            if (stream == null) throw new IllegalStateException("Importer XML missing from test classpath");
            Files.copy(stream, changelog);
        }
        csv = root.resolve("config/liquibase/data/questions_fact_fiction.csv");
        Files.createDirectories(csv.getParent());
        Files.writeString(csv, BANK);
    }

    void migrate() throws Exception {
        var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        try (var resources = new DirectoryResourceAccessor(root)) {
            // Do not close Liquibase here: the tests query the same connection afterward.
            new Liquibase("import.xml", resources, database).update(new Contexts(), new LabelExpression());
        }
    }

    long count(String table) throws Exception {
        try (var statement = connection.createStatement(); var result = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            result.next();
            return result.getLong(1);
        }
    }

    @AfterEach
    void close() throws Exception {
        if (connection != null) connection.close();
    }

    @Test
    void importsCorrectChoicesExplanationsAndLinks() throws Exception {
        migrate();
        assertEquals(2, count("question"));
        assertEquals(2, count("feedback"));
        assertEquals(4, count("answer"));
        assertEquals(2, count("fof_question"));
        try (
            var statement = connection.createStatement();
            var result = statement.executeQuery(
                "SELECT a.id,a.text,a.correct,f.explanation,a.next_stage_id,a.terminal_resolution FROM answer a JOIN feedback f ON f.id=a.feedback_id ORDER BY a.id"
            )
        ) {
            for (int i = 0; i < 4; i++) {
                assertTrue(result.next());
                assertEquals(40202L + i, result.getLong(1));
                assertEquals(i % 2 == 0 ? "True" : "False", result.getString(2));
                assertEquals(i == 0 || i == 3, result.getBoolean(3));
                assertEquals(i < 2 ? "An explanation; with a semicolon." : "It says \"False\".", result.getString(4));
                assertNull(result.getObject(5));
                assertNull(result.getObject(6));
            }
        }
    }

    @Test
    void appliedMigrationDoesNotRepeat() throws Exception {
        migrate();
        migrate();
        assertEquals(2, count("question"));
        assertEquals(4, count("answer"));
    }

    @Test
    void invalidBooleanRejectsWholeBank() throws Exception {
        Files.writeString(csv, BANK.replace(";false;", ";perhaps;"));
        assertThrows(Exception.class, this::migrate);
        assertEquals(0, count("question"));
    }

    @Test
    void duplicateQuestionRejectsWholeBank() throws Exception {
        Files.writeString(csv, BANK.replace("20102;", "20101;"));
        assertThrows(Exception.class, this::migrate);
        assertEquals(0, count("question"));
    }

    @Test
    void missingReferenceRollsBackEarlierRows() throws Exception {
        Files.writeString(csv, BANK.replace("20102;10;201;", "20102;10;999;"));
        assertThrows(Exception.class, this::migrate);
        assertEquals(0, count("question"));
        assertEquals(0, count("feedback"));
        assertEquals(0, count("answer"));
        assertEquals(0, count("fof_question"));
    }

    @Test
    void occupiedAnswerIdIsPreservedAndImportRollsBack() throws Exception {
        try (var statement = connection.createStatement()) {
            statement.execute("INSERT INTO answer(id,text,correct) VALUES(40204,'Existing answer',TRUE)");
        }
        assertThrows(Exception.class, this::migrate);
        assertEquals(0, count("question"));
        assertEquals(0, count("feedback"));
        assertEquals(1, count("answer"));
        assertEquals(0, count("fof_question"));
    }

    @Test
    void oversizedTextRejectsWholeBank() throws Exception {
        Files.writeString(csv, BANK.replace("A sample false statement.", "x".repeat(256)));
        assertThrows(Exception.class, this::migrate);
        assertEquals(0, count("question"));
    }
}
