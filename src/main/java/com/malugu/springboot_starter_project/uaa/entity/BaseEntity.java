package com.malugu.springboot_starter_project.uaa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

	@Audited
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonSetter
	private Long id;

	@Audited
	@Column(name = "uuid", unique = true)
	private String uuid = UUID.randomUUID().toString();

	@Audited
	@CreationTimestamp
	@JsonIgnore
	private LocalDateTime createdAt;

	@Audited
	@UpdateTimestamp
	@JsonIgnore
	private LocalDateTime updatedAt;

	@Audited
	@CreatedBy
	private Long createdBy;

	@Audited
	@LastModifiedBy
	private Long updatedBy;

	@Audited
	@Column(columnDefinition = "boolean default true")
	private boolean active = true;

	@Audited
	@Column(columnDefinition = "boolean default false")
	private boolean deleted;
}
