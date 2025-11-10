package com.malugu.springboot_starter_project.uaa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import tz.go.ega.uaa.entity.Institution;

import java.util.Optional;

public interface InstitutionRepository
		extends JpaRepository<Institution, Long>, RevisionRepository<Institution, Long, Long> {

	public Optional<Institution> findFirstByUuid(String uuid);

	public Optional<Institution> findFirstByAcronym(String uuid);

	public Optional<Institution> findFirstByAcronymIgnoreCase(String acronym);
}
