package org.lubas.domain.repository;


import org.lubas.domain.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findUserByUsername(String username);
    Boolean existsByUsername(String username);






}
