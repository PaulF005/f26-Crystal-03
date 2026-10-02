package com.crystal.kip.repository.LinkRepo;

import com.crystal.kip.repository.ACustomRepoCode.ACustomConcpetProgressRepository;
import com.crystal.kip.repository.ConceptProgressRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface LinkConcpetProgressRepository extends ConceptProgressRepository, ACustomConcpetProgressRepository {
    //Left intentionally blank, use this in application code
}
