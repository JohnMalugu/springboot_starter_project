package com.malugu.springboot_starter_project.uaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;
import com.malugu.springboot_starter_project.uaa.entity.UserAccount;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long>, RevisionRepository<UserAccount, Long, Long> {
	Optional<UserAccount> findFirstByUsername(String username);

	Optional<UserAccount> findFirstByUuid(String uuid);
}
