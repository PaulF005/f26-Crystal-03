package com.crystal.kip.repository.LinkRepo;

import com.crystal.kip.repository.CustomRepoCode.CustomUserProfileRepository;
import com.crystal.kip.repository.UserProfileRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface LinkUserProfileRepository extends UserProfileRepository, CustomUserProfileRepository {
    // Left intentionally blank, use this in application code
}
