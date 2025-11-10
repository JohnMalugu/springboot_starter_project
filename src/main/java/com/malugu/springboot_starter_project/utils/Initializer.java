package com.malugu.springboot_starter_project.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tz.go.ega.uaa.entity.Institution;
import tz.go.ega.uaa.entity.Permission;
import tz.go.ega.uaa.entity.Role;
import tz.go.ega.uaa.entity.UserAccount;
import tz.go.ega.uaa.enums.UserAuthenticationType;
import tz.go.ega.uaa.repository.InstitutionRepository;
import tz.go.ega.uaa.repository.PermissionRepository;
import tz.go.ega.uaa.repository.RoleRepository;
import tz.go.ega.uaa.repository.UserAccountRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Initializer implements ApplicationRunner {

	@Autowired
	private InstitutionRepository institutionRepository;

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		Institution institution = new Institution();
		Optional<Institution> optionalInst = institutionRepository.findFirstByAcronymIgnoreCase("eGA");
		if (optionalInst.isPresent()) {
			institution = optionalInst.get();
		}
		institution.setAcronym("eGA");
		institution.setName("e-Government Authority");
		institution.setEmail("info@ega.go.tz");
		institution.setLdapIp("ldap://10.1.2.100:389");
		institution.setReferenceNumber("INS0000000TR57");
		institution.setVoteCode("TR57");
		institution.setWebSite("www.ega.go.tz");
		institution.setAuthenticationType(UserAuthenticationType.Database);
		institutionRepository.save(institution);

		Role role = new Role();
		Optional<Role> roleO = roleRepository.findFirstByName("SUPER_ADMIN");
		if (roleO.isPresent()) {
			role = roleO.get();
		}
		role.setName("SUPER_ADMIN");
		role.setDisplayName("SUPER ADMIN");
		role.setDescription("SUPER ADMIN");
		roleRepository.save(role);

		UserAccount userAccount = new UserAccount();
		Optional<UserAccount> account = userAccountRepository.findFirstByUsername("admin@ega.go.tz");
		if (account.isPresent()) {
			userAccount = account.get();
		}
		userAccount.setPassword(passwordEncoder.encode("12345678"));
		userAccount.setRoles(List.of(role));
		userAccount.setInstitution(institution);
		userAccount.setUsername("admin@ega.go.tz");
		userAccount.setAuthenticationType(UserAuthenticationType.Database);
		userAccountRepository.save(userAccount);

		UserAccount userAccount2 = new UserAccount();
		Optional<UserAccount> account2 = userAccountRepository.findFirstByUsername("info@ega.go.tz");
		if (account2.isPresent()) {
			userAccount2 = account2.get();
		}
		userAccount2.setPassword(passwordEncoder.encode("12345678"));
		userAccount2.setRoles(List.of(role));
		userAccount2.setInstitution(institution);
		userAccount2.setUsername("info@ega.go.tz");
		userAccount2.setAuthenticationType(UserAuthenticationType.Database);
		userAccountRepository.save(userAccount2);

		UserAccount userAccount3 = new UserAccount();
		Optional<UserAccount> account3 = userAccountRepository.findFirstByUsername("test.dio@egatest.go.tz");
		if (account3.isPresent()) {
			userAccount3 = account3.get();
		}
		userAccount3.setPassword(passwordEncoder.encode("Helpdesk@20_23"));
		userAccount3.setRoles(List.of(role));
		userAccount3.setInstitution(institution);
		userAccount3.setUsername("test.dio@egatest.go.tz");
		userAccount3.setAuthenticationType(UserAuthenticationType.Database);
		userAccountRepository.save(userAccount3);
	}

	private void savePermission() {
		try {
			List<Permission> permissions = new ArrayList<>();
			permissions.add(new Permission("ROLE_USER", "User", "USER"));
			permissions.add(new Permission("ROLE_USER_CREATE", "Create User", "USER"));
			permissions.add(new Permission("ROLE_ROLE_CREATE", "Create Role", "ROLE"));
			permissions.add(new Permission("ROLE_ROLE_LIST", "List Roles", "ROLE"));
			for (Permission permission : permissions) {
				Permission permissionNew = new Permission();
				Optional<Permission> roleO = permissionRepository.findFirstByName(permission.getName());
				if (roleO.isPresent()) {
					permissionNew = roleO.get();
				}
				permissionNew.setName(permission.getName());
				permissionNew.setDisplayName(permission.getDisplayName());
				permissionNew.setGroupName(permission.getGroupName());
				permissionRepository.save(permissionNew);
			}
			List<Permission> permissions2 = permissionRepository.findAll();
			Optional<Role> roleO = roleRepository.findFirstByName("SUPER_ADMIN");
			if (roleO.isPresent()) {
				roleO.get().setPermissions(permissions2);
				roleRepository.save(roleO.get());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
