package com.crystal.kip.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface LinkTopicProgressRepository extends TopicProgressRepository, ACustomTopicProgressRepository {
    //Left intentionally blank, use this for code
}
