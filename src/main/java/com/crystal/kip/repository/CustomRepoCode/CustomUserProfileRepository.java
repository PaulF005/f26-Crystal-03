package com.crystal.kip.repository.CustomRepoCode;

import com.crystal.kip.domain.UserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserProfileRepository {
    @Query("SELECT up FROM UserProfile up " + "INNER JOIN up.dataUser u " + "WHERE u.login = :login")
    Optional<UserProfile> findOneByDataUserLogin(@Param("login") String login);
}
