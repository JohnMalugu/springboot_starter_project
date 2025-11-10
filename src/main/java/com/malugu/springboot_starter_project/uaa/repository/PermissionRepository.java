package com.malugu.springboot_starter_project.uaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import tz.go.ega.uaa.entity.Permission;

import java.util.Optional;

public interface PermissionRepository
		extends JpaRepository<Permission, Long>, RevisionRepository<Permission, Long, Long> {
	Optional<Permission> findFirstByUuid(String uuid);

	Optional<Permission> findFirstByName(String name);
}
