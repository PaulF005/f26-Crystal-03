package com.crystal.kip.config;

import com.crystal.kip.service.sourcecheck.SourceCheckService;
import com.crystal.kip.service.sourcecheck.SourceChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Checks registered sources once whenever this backend application starts. */
@Component
public class SourceCheckStartup {

    private static final Logger LOG = LoggerFactory.getLogger(SourceCheckStartup.class);
    private final SourceCheckService sources;
    private final ApplicationProperties properties;

    public SourceCheckStartup(SourceCheckService sources, ApplicationProperties properties) {
        this.sources = sources;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkSources() {
        if (Boolean.TRUE.equals(properties.getLiquibase().getAsyncStart())) {
            LOG.warn("Source check skipped: set application.liquibase.async-start=false so migrations finish before source checks");
            return;
        }
        try {
            var reports = sources.checkAll();
            if (reports.isEmpty()) LOG.info("Source check: no source URLs are registered");
            for (var report : reports) {
                if (report.error() != null) {
                    LOG.warn("Source {} could not be checked: {}", report.sourceId(), report.error());
                } else if (report.result().status() == SourceChecker.Status.CHANGED) {
                    LOG.warn(
                        "Source {}: {} FOF questions for manual review: {}",
                        report.sourceId(),
                        report.result().message(),
                        report.affectedQuestionIds()
                    );
                } else {
                    LOG.info("Source {}: {}", report.sourceId(), report.result().message());
                }
            }
        } catch (Exception e) {
            // Source availability must not stop the game server from starting.
            LOG.warn("Source checks could not run: {}", e.getMessage());
        }
    }
}
