package com.crystal.kip.repository.LinkRepo;

import com.crystal.kip.repository.CustomRepoCode.CustomGameProgressRepository;
import com.crystal.kip.repository.GameProgressRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface LinkGameProgressRepository extends GameProgressRepository, CustomGameProgressRepository {
    //Left intentionally blank, use this in application code
}
