package com.crystal.kip.config;

import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Caffeine caffeine = jHipsterProperties.getCache().getCaffeine();

        CaffeineConfiguration<Object, Object> caffeineConfiguration = new CaffeineConfiguration<>();
        caffeineConfiguration.setMaximumSize(OptionalLong.of(caffeine.getMaxEntries()));
        caffeineConfiguration.setExpireAfterWrite(OptionalLong.of(TimeUnit.SECONDS.toNanos(caffeine.getTimeToLiveSeconds())));
        caffeineConfiguration.setStatisticsEnabled(true);
        jcacheConfiguration = caffeineConfiguration;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.crystal.kip.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.crystal.kip.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.crystal.kip.domain.User.class.getName());
            createCache(cm, com.crystal.kip.domain.Authority.class.getName());
            createCache(cm, com.crystal.kip.domain.User.class.getName() + ".authorities");
            createCache(cm, com.crystal.kip.domain.UserProfile.class.getName());
            createCache(cm, com.crystal.kip.domain.UserProfile.class.getName() + ".topicProgresseses");
            createCache(cm, com.crystal.kip.domain.UserProfile.class.getName() + ".gameProgresseses");
            createCache(cm, com.crystal.kip.domain.UserProfile.class.getName() + ".conceptProgresseses");
            createCache(cm, com.crystal.kip.domain.Topic.class.getName());
            createCache(cm, com.crystal.kip.domain.Topic.class.getName() + ".concepts");
            createCache(cm, com.crystal.kip.domain.Concept.class.getName());
            createCache(cm, com.crystal.kip.domain.Concept.class.getName() + ".questions");
            createCache(cm, com.crystal.kip.domain.Question.class.getName());
            createCache(cm, com.crystal.kip.domain.Question.class.getName() + ".answers");
            createCache(cm, com.crystal.kip.domain.Answer.class.getName());
            createCache(cm, com.crystal.kip.domain.Feedback.class.getName());
            createCache(cm, com.crystal.kip.domain.Game.class.getName());
            createCache(cm, com.crystal.kip.domain.Game.class.getName() + ".scenarios");
            createCache(cm, com.crystal.kip.domain.Scenario.class.getName());
            createCache(cm, com.crystal.kip.domain.Scenario.class.getName() + ".stages");
            createCache(cm, com.crystal.kip.domain.Stage.class.getName());
            createCache(cm, com.crystal.kip.domain.LegalContent.class.getName());
            createCache(cm, com.crystal.kip.domain.Source.class.getName());
            createCache(cm, com.crystal.kip.domain.TopicProgress.class.getName());
            createCache(cm, com.crystal.kip.domain.GameProgress.class.getName());
            createCache(cm, com.crystal.kip.domain.GameSession.class.getName());
            createCache(cm, com.crystal.kip.domain.GameSession.class.getName() + ".stageAttempts");
            createCache(cm, com.crystal.kip.domain.StageAttempt.class.getName());
            createCache(cm, com.crystal.kip.domain.ConceptProgress.class.getName());
            createCache(cm, com.crystal.kip.domain.UserDetail.class.getName());
            createCache(cm, com.crystal.kip.domain.UserDetail.class.getName() + ".progresses");
            createCache(cm, com.crystal.kip.domain.Progress.class.getName());
            // jhipster-needle-caffeine-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }
}
