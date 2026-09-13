package com.crystal.kip;

import com.crystal.kip.config.AsyncSyncConfiguration;
import com.crystal.kip.config.DatabaseTestcontainer;
import com.crystal.kip.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        KipApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        com.crystal.kip.config.JacksonHibernateConfiguration.class,
    }
)
@ImportTestcontainers(DatabaseTestcontainer.class)
public @interface IntegrationTest {}
