package com.crystal.kip.repository.LinkRepo;

import com.crystal.kip.repository.CustomRepoCode.CustomTopicProgressRepository;
import com.crystal.kip.repository.TopicProgressRepository;
import java.time.Instant;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface LinkTopicProgressRepository extends TopicProgressRepository, CustomTopicProgressRepository {
    //Left intentionally blank, use this in application code
}
