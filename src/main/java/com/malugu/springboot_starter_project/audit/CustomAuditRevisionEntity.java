package com.malugu.springboot_starter_project.audit;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@RevisionEntity(AuditRevisionListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "REVINFO", schema = "audit")
public class CustomAuditRevisionEntity implements Serializable {

	private static final long serialVersionUID = 8530213963961662300L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@RevisionNumber
	@Column(name = "id", nullable = false)
	private Long id;

	@RevisionTimestamp
	@Column(name = "REVTSTMP")
	private long timestamp;

	@Column(name = "USERNAME")
	private String username;

	private String ipAddress;

	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "event_date_time")
	@CreationTimestamp
	private LocalDateTime eventDateTime;

//	@OneToMany(mappedBy = "revision", cascade = { CascadeType.PERSIST,
//			CascadeType.REMOVE }/* , fetch = FetchType.EAGER */)
//	private Set<CustomerModifiedEntityTypeEntity> modifiedEntityTypes = new HashSet<>();

//	public void addModifiedEntityType(String entityClassName, String simpleClassName) {
//		modifiedEntityTypes.add(new CustomerModifiedEntityTypeEntity(this, entityClassName, simpleClassName));
//	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public Date getRevisionDate() {
		return new Date(timestamp);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CustomAuditRevisionEntity)) {
			return false;
		}

		final CustomAuditRevisionEntity that = (CustomAuditRevisionEntity) o;
		return id == that.getId() && timestamp == that.getTimestamp();
	}

}
