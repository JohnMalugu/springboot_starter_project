package com.malugu.springboot_starter_project.uaa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import com.malugu.springboot_starter_project.uaa.enums.UserAuthenticationType;

import java.io.Serial;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "institutions")
@Audited
public class Institution extends BaseEntity{
    @Serial
    private static final long serialVersionUID = 1L;

	private String referenceNumber;
	private String name;
	private String voteCode;
	private String email;

	@Column(unique = true)
	private String acronym;
	private String webSite;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(255) default 'Database'")
	@JsonIgnore
	private UserAuthenticationType authenticationType;

	@JsonIgnore
	private String ldapIp;

	@OneToMany(mappedBy = "institution")
	private List<UserAccount> userAccounts;
}
