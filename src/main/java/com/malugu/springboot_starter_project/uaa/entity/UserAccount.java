package com.malugu.springboot_starter_project.uaa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.malugu.springboot_starter_project.uaa.enums.UserAuthenticationType;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_accounts")
@Audited
public class UserAccount extends BaseEntity implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

	private String username;

	@JsonIgnore
	private String password;

	@JoinTable(name = "role_user", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "role_id", referencedColumnName = "id") })
	@ManyToMany(fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Role> roles;

	@ManyToOne
	private Institution institution;

	@CreationTimestamp
	@JsonIgnore
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@JsonIgnore
	private LocalDateTime updatedAt;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(255) default 'Database'")
	private UserAuthenticationType authenticationType;

	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (roles == null || roles.isEmpty()) {
			return List.of();
		}
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (Role role : this.roles) {
			if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
				for (Permission permission : role.getPermissions()) {
					SimpleGrantedAuthority authority = new SimpleGrantedAuthority(permission.getName());
					authorities.add(authority);
				}
			}
		}
		return authorities;
	}
	
}
