package com.malugu.springboot_starter_project.uaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import com.malugu.springboot_starter_project.uaa.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, RevisionRepository<Role, Long, Long> {
	Optional<Role> findFirstByUuid(String uuid);

	Optional<Role> findFirstByName(String name);
}
