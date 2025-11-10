package com.malugu.springboot_starter_project.uaa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.util.List;

@Audited
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permissions")
public class Permission extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

	@Column(unique = true)
	private String name;

	@Column(name = "group_name")
	private String groupName;

	@Column(name = "display_name")
	private String displayName;

	@ManyToMany(mappedBy = "permissions")
	@JsonIgnore
	private List<Role> roles;

	public Permission(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}

	public Permission(String name, String displayName, String groupName) {
		this.name = name;
		this.displayName = displayName;
		this.groupName = groupName;
	}


}
