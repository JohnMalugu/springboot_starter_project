package com.malugu.springboot_starter_project.uaa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Audited
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class Role extends BaseEntity{

    @Serial
    private static final long serialVersionUID = 1L;

	@NotEmpty(message = "Role name is required field")
	private String name;

	@Column(name = "display_name")
	@NotEmpty(message = "Display name is required field.")
	private String displayName;

	@NotEmpty(message = "Role description is required field.")
	private String description;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "permission_role", joinColumns = {
			@JoinColumn(name = "role_id", referencedColumnName = "id"), }, inverseJoinColumns = {
					@JoinColumn(name = "permission_id", referencedColumnName = "id") })
	private List<Permission> permissions = new ArrayList<>();

	public Role(String name, String displayName, String description) {
		this.name = name;
		this.displayName = displayName;
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Role))
			return false;
		Role role = (Role) o;
		return getId().equals(role.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
