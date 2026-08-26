package com.myapp.repository;

import com.myapp.domain.UserDetail;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserDetail entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {}
