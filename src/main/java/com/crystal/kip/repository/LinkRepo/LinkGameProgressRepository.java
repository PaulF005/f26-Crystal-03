package com.crystal.kip.repository.LinkRepo;

import com.crystal.kip.repository.ACustomRepoCode.ACustomGameProgressRepository;
import com.crystal.kip.repository.GameProgressRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkGameProgressRepository extends GameProgressRepository, ACustomGameProgressRepository {
    //Left intentionally blank, use this in application code
}
