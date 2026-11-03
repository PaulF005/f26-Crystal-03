package com.crystal.kip.service.fof;

import static org.junit.jupiter.api.Assertions.*;

import com.crystal.kip.config.ApplicationProperties;
import com.crystal.kip.config.SourceCheckStartup;
import com.crystal.kip.domain.FactFictionHistory;
import com.crystal.kip.repository.fof.FactFictionRepository;
import com.crystal.kip.repository.sourcecheck.SourceCheckRepository;
import com.crystal.kip.service.curation.QuestionCurator;
import com.crystal.kip.service.sourcecheck.SourceCheckService;
import com.crystal.kip.service.sourcecheck.SourceChecker;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.core.H2Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Uses real migrations and the imported bank in an isolated database, never the project's MySQL. */
class FactFictionConnectionsTest {

    static class ControlledChecker extends SourceChecker {

        String hash = "a".repeat(64);
        boolean fail;
        int calls;

        ControlledChecker(Path path) {
            super(path.toString());
        }

        @Override
        public String getFingerprint(String url) throws IOException {
            calls++;
            if (fail) throw new IOException("Simulated download failure");
            return hash;
        }
    }

    @TestConfiguration
    @EnableTransactionManagement
    @Import({
        FactFictionRepository.class,
        CurrentPlayerService.class,
        FactFictionPracticeService.class,
        QuestionHistoryService.class,
        QuestionCurator.class,
        SourceCheckRepository.class,
        SourceCheckService.class,
        SourceCheckStartup.class,
    })
    static class Config {

        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource source) {
            return new JdbcTemplate(source);
        }

        @Bean
        PlatformTransactionManager transactionManager(DataSource source) {
            return new DataSourceTransactionManager(source);
        }

        @Bean
        ApplicationProperties applicationProperties() {
            var properties = new ApplicationProperties();
            properties.getLiquibase().setAsyncStart(false);
            return properties;
        }

        @Bean
        ControlledChecker sourceChecker() throws IOException {
            return new ControlledChecker(Files.createTempDirectory("fof-fingerprint-test-"));
        }
    }

    static class Fixture implements AutoCloseable {

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        final JdbcTemplate jdbc = context.getBean(JdbcTemplate.class);

        Fixture() throws Exception {
            for (String sql : List.of(
                "CREATE TABLE game(id BIGINT PRIMARY KEY,name VARCHAR(255))",
                "CREATE TABLE topic(id BIGINT PRIMARY KEY,name VARCHAR(255),explanation VARCHAR(255))",
                "CREATE TABLE concept(id BIGINT PRIMARY KEY,name VARCHAR(255),explanation VARCHAR(255),topic_id BIGINT REFERENCES topic(id))",
                "CREATE TABLE source(id BIGINT PRIMARY KEY,name VARCHAR(255),url VARCHAR(255),date VARCHAR(255),data VARCHAR(255))",
                "CREATE TABLE question(id BIGINT PRIMARY KEY,question VARCHAR(255),concept_id BIGINT NOT NULL REFERENCES concept(id))",
                "CREATE TABLE feedback(id BIGINT PRIMARY KEY,feedback VARCHAR(255),explanation VARCHAR(255))",
                "CREATE TABLE answer(id BIGINT PRIMARY KEY,text VARCHAR(255),correct BOOLEAN,question_id BIGINT REFERENCES question(id),feedback_id BIGINT REFERENCES feedback(id),next_stage_id BIGINT,terminal_resolution VARCHAR(255),outcome_text VARCHAR(255))",
                "CREATE TABLE jhi_user(id BIGINT PRIMARY KEY,login VARCHAR(255))",
                "CREATE TABLE user_profile(id BIGINT PRIMARY KEY,data_user_id BIGINT UNIQUE REFERENCES jhi_user(id))"
            ))
                jdbc.execute(sql);
            migrate();
            jdbc.update("INSERT INTO jhi_user VALUES(1,'alice'),(2,'bob'),(3,'no-profile')");
            jdbc.update("INSERT INTO user_profile VALUES(1,1),(2,2)");
            login("alice");
        }

        void migrate() throws Exception {
            try (var connection = context.getBean(DataSource.class).getConnection(); var resources = new ClassLoaderResourceAccessor()) {
                var database = new H2Database();
                database.setConnection(new JdbcConnection(connection));
                new Liquibase("fof-connections-test.xml", resources, database).update(new Contexts("dev"), new LabelExpression());
            }
        }

        FactFictionPracticeService practice() {
            return context.getBean(FactFictionPracticeService.class);
        }

        QuestionHistoryService history() {
            return context.getBean(QuestionHistoryService.class);
        }

        FactFictionRepository repository() {
            return context.getBean(FactFictionRepository.class);
        }

        ControlledChecker checker() {
            return context.getBean(ControlledChecker.class);
        }

        long count(String table) {
            return jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
        }

        @Override
        public void close() {
            SecurityContextHolder.clearContext();
            jdbc.execute("SHUTDOWN");
            context.close();
        }
    }

    static void login(String username) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, "", List.of()));
    }

    @Test
    void retrievesRealBanksAndSelectsTenWithoutWritingHistory() throws Exception {
        try (var f = new Fixture()) {
            assertEquals(75, f.count("question"));
            assertEquals(150, f.count("answer"));
            for (long concept : List.of(201L, 202L, 203L)) {
                assertEquals(25, f.repository().bank(10, 104, concept).size());
                var prompts = f.practice().prepare(10, 104, concept);
                assertEquals(10, prompts.size());
                assertEquals(10, prompts.stream().map(FactFictionPracticeService.Prompt::questionId).distinct().count());
                assertTrue(prompts.stream().allMatch(p -> p.choices().size() == 2));
            }
            assertEquals(0, f.count("question_history"));
            f.migrate();
            assertEquals(75, f.count("question"));
            assertThrows(IllegalArgumentException.class, () -> f.practice().prepare(11, 104, 201));
            assertThrows(IllegalArgumentException.class, () -> f.practice().prepare(10, 999, 201));
        }
    }

    @Test
    void historyPersistsAndChangesWhatTheCuratorSelects() throws Exception {
        try (var f = new Fixture()) {
            UUID session = UUID.randomUUID();
            UUID presentation = UUID.randomUUID();
            f.history().recordShown(session, 20101, presentation);
            var result = f.history().recordAnswer(session, presentation, 40203);
            assertFalse(result.correct());
            assertFalse(result.explanation().isBlank());
            assertEquals(new FactFictionHistory(1, 0, 1), f.repository().history(1).get(20101L));
            assertEquals(20101, f.practice().prepare(10, 104, 201).getFirst().questionId());
            // Reconstructing the repository still reads the same persisted data.
            assertEquals(new FactFictionHistory(1, 0, 1), new FactFictionRepository(f.jdbc).history(1).get(20101L));
            login("bob");
            assertTrue(f.repository().history(2).isEmpty());
            assertEquals(10, f.practice().prepare(10, 104, 201).size());
        }
    }

    @Test
    void repeatedDisplaysAndAnswersCountOnlyOnce() throws Exception {
        try (var f = new Fixture()) {
            UUID session = UUID.randomUUID(),
                presentation = UUID.randomUUID();
            f.history().recordShown(session, 20101, presentation);
            f.history().recordShown(session, 20101, presentation);
            assertFalse(f.history().recordAnswer(session, presentation, 40202).duplicate());
            assertTrue(f.history().recordAnswer(session, presentation, 40202).duplicate());
            assertEquals(new FactFictionHistory(1, 1, 0), f.repository().history(1).get(20101L));
            assertThrows(IllegalStateException.class, () -> f.history().recordAnswer(session, presentation, 40203));
            f.history().recordShown(session, 20101, UUID.randomUUID());
            assertEquals(new FactFictionHistory(2, 1, 0), f.repository().history(1).get(20101L));
        }
    }

    @Test
    void rejectsOtherPlayersSessionsAndUnrelatedAnswers() throws Exception {
        try (var f = new Fixture()) {
            UUID session = UUID.randomUUID(),
                presentation = UUID.randomUUID();
            f.history().recordShown(session, 20101, presentation);
            login("bob");
            assertThrows(IllegalArgumentException.class, () -> f.history().recordAnswer(session, presentation, 40202));
            assertThrows(IllegalArgumentException.class, () -> f.history().recordShown(session, 20101, presentation));
            login("alice");
            assertThrows(IllegalArgumentException.class, () -> f.history().recordAnswer(UUID.randomUUID(), presentation, 40202));
            assertThrows(IllegalArgumentException.class, () -> f.history().recordAnswer(session, presentation, 40204));
            assertThrows(IllegalArgumentException.class, () -> f.history().recordShown(session, 20102, presentation));
            assertEquals(new FactFictionHistory(1, 0, 0), f.repository().history(1).get(20101L));
            assertTrue(f.repository().history(2).isEmpty());
        }
    }

    @Test
    void failedHistoryUpdateRollsBackTheAcceptedAnswer() throws Exception {
        try (var f = new Fixture()) {
            UUID session = UUID.randomUUID(),
                presentation = UUID.randomUUID();
            f.history().recordShown(session, 20101, presentation);
            f.jdbc.update("DELETE FROM question_history");
            assertThrows(IllegalStateException.class, () -> f.history().recordAnswer(session, presentation, 40202));
            assertNull(f.repository().presentation(presentation).orElseThrow().selectedAnswer());
        }
    }

    @Test
    void concurrentRetriesDoNotInflateCounts() throws Exception {
        try (var f = new Fixture(); var executor = Executors.newFixedThreadPool(2)) {
            UUID session = UUID.randomUUID(),
                presentation = UUID.randomUUID();
            Callable<Void> attempt = () -> {
                login("alice");
                try {
                    f.history().recordShown(session, 20101, presentation);
                    f.history().recordAnswer(session, presentation, 40202);
                    return null;
                } finally {
                    SecurityContextHolder.clearContext();
                }
            };
            for (var future : executor.invokeAll(List.of(attempt, attempt))) future.get();
            assertEquals(new FactFictionHistory(1, 1, 0), f.repository().history(1).get(20101L));
        }
    }

    @Test
    void rejectsMissingAuthenticationProfilesAndMalformedBanks() throws Exception {
        try (var f = new Fixture()) {
            SecurityContextHolder.clearContext();
            assertThrows(IllegalStateException.class, () -> f.practice().prepare(10, 104, 201));
            login("no-profile");
            assertThrows(IllegalStateException.class, () -> f.practice().prepare(10, 104, 201));
            login("alice");
            f.jdbc.update("UPDATE answer SET correct=TRUE WHERE question_id=20101");
            assertThrows(IllegalStateException.class, () -> f.practice().prepare(10, 104, 201));
        }
    }

    @Test
    void checksRegisteredSourcesAndFindsAffectedQuestions() throws Exception {
        try (var f = new Fixture()) {
            var service = f.context.getBean(SourceCheckService.class);
            assertEquals(SourceChecker.Status.FIRST_CHECK, service.checkAll().getFirst().result().status());
            assertEquals(SourceChecker.Status.UNCHANGED, service.checkAll().getFirst().result().status());
            f.checker().hash = "b".repeat(64);
            var change = service.checkAll().getFirst();
            assertEquals(SourceChecker.Status.CHANGED, change.result().status());
            assertEquals(75, change.affectedQuestionIds().size());
            f.checker().fail = true;
            assertNotNull(service.checkAll().getFirst().error());
            f.checker().fail = false;
            assertEquals(SourceChecker.Status.UNCHANGED, service.checkAll().getFirst().result().status());
        }
    }

    @Test
    void sharedUrlReportsChangesForEveryLinkedSource() throws Exception {
        try (var f = new Fixture()) {
            f.jdbc.update("INSERT INTO source(id,name,url,data) SELECT 5,'Same document',url,'test' FROM source WHERE id=4");
            f.jdbc.update("UPDATE fof_question SET source_id=5 WHERE question_id IN (SELECT id FROM question WHERE concept_id=203)");
            var service = f.context.getBean(SourceCheckService.class);
            service.checkAll();
            assertEquals(1, f.checker().calls);
            f.checker().hash = "b".repeat(64);
            var changes = service.checkAll();
            assertEquals(2, f.checker().calls);
            assertEquals(2, changes.size());
            assertTrue(changes.stream().allMatch(r -> r.result().status() == SourceChecker.Status.CHANGED));
            assertEquals(
                75,
                changes
                    .stream()
                    .mapToInt(r -> r.affectedQuestionIds().size())
                    .sum()
            );
        }
    }

    @Test
    void readyEventRunsChecksButAsyncMigrationsAreGuarded() throws Exception {
        try (var f = new Fixture()) {
            assertEquals(0, f.checker().calls);
            var event = new ApplicationReadyEvent(new SpringApplication(Config.class), new String[0], f.context, Duration.ZERO);
            f.context.publishEvent(event);
            assertEquals(1, f.checker().calls);
            f.context.getBean(ApplicationProperties.class).getLiquibase().setAsyncStart(true);
            f.context.publishEvent(event);
            assertEquals(1, f.checker().calls);
            f.context.getBean(ApplicationProperties.class).getLiquibase().setAsyncStart(false);
            f.checker().fail = true;
            assertDoesNotThrow(() -> f.context.publishEvent(event));
        }
    }
}
