package com.crystal.kip.service.sourcecheck;

import static org.junit.jupiter.api.Assertions.*;

import com.crystal.kip.service.curation.QuestionCurator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

class SourceCheckerTest {

    @TempDir
    Path directory;

    private static final String URL = "https://example.test/manual.pdf";

    // Control download outcomes so these tests need neither internet access nor a live DMV server.
    private static class StubChecker extends SourceChecker {

        String fingerprint = "a".repeat(64);
        boolean fail;

        StubChecker(Path directory) {
            super(directory.toString());
        }

        @Override
        public String getFingerprint(String url) throws IOException {
            if (fail) throw new IOException("Simulated download failure");
            return fingerprint;
        }
    }

    @Test
    void savesBaselineAndReusesItAcrossInstances() throws Exception {
        var checker = new StubChecker(directory);
        assertEquals(SourceChecker.Status.FIRST_CHECK, checker.checkSource(URL).status());
        assertEquals(SourceChecker.Status.UNCHANGED, new StubChecker(directory).checkSource(URL).status());
    }

    @Test
    void reportsChangeThenUsesLatestSuccessfulBaseline() throws Exception {
        var checker = new StubChecker(directory);
        checker.checkSource(URL);
        checker.fingerprint = "b".repeat(64);
        assertEquals(SourceChecker.Status.CHANGED, checker.checkSource(URL).status());
        assertEquals(SourceChecker.Status.UNCHANGED, checker.checkSource(URL).status());
    }

    @Test
    void failedDownloadPreservesBaseline() throws Exception {
        var checker = new StubChecker(directory);
        checker.checkSource(URL);
        checker.fail = true;
        assertThrows(IOException.class, () -> checker.checkSource(URL));
        checker.fail = false;
        assertEquals(SourceChecker.Status.UNCHANGED, checker.checkSource(URL).status());
    }

    @Test
    void keepsDifferentUrlsSeparate() throws Exception {
        var checker = new StubChecker(directory);
        checker.checkSource(URL);
        assertEquals(SourceChecker.Status.FIRST_CHECK, checker.checkSource("https://example.test/code").status());
        assertEquals(SourceChecker.Status.UNCHANGED, checker.checkSource(URL).status());
        try (var files = Files.list(directory)) {
            assertEquals(2, files.count());
        }
    }

    @Test
    void rejectsInvalidUrlsAndCorruptSavedFingerprints() throws Exception {
        var checker = new StubChecker(directory);
        assertThrows(IllegalArgumentException.class, () -> checker.checkSource("file:///tmp/example"));
        assertThrows(IllegalArgumentException.class, () -> checker.checkSource("https://user:password@example.test/code"));
        checker.checkSource(URL);
        Path saved;
        try (var files = Files.list(directory)) {
            saved = files.findFirst().orElseThrow();
        }
        Files.writeString(saved, "corrupt");
        assertThrows(IOException.class, () -> checker.checkSource(URL));
        assertEquals("corrupt", Files.readString(saved));
    }

    @Test
    void springCreatesBothServicesWithoutRunningDownloads() {
        try (var context = new AnnotationConfigApplicationContext()) {
            context
                .getEnvironment()
                .getPropertySources()
                .addFirst(new MapPropertySource("test", Map.of("kip.source-checker.storage-directory", directory.toString())));
            context.register(QuestionCurator.class, SourceChecker.class);
            context.refresh();
            assertNotNull(context.getBean(QuestionCurator.class));
            assertNotNull(context.getBean(SourceChecker.class));
            assertDoesNotThrow(() -> {
                try (var files = Files.list(directory)) {
                    assertEquals(0, files.count());
                }
            });
        }
    }
}
