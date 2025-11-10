package com.malugu.springboot_starter_project.uaa.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.malugu.springboot_starter_project.uaa.entity.Role;
import com.malugu.springboot_starter_project.uaa.entity.UserAccount;
import com.malugu.springboot_starter_project.uaa.repository.RoleRepository;
import com.malugu.springboot_starter_project.uaa.repository.UserAccountRepository;

import java.util.*;

@Service
public class UserAccountServiceImpl {

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	public Map<String, Object> UserAccountInfo() {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<>();
	}

	public Optional<UserAccount> findByUsername(String username) {
		return userAccountRepository.findFirstByUsername(username);
	}

	public UserAccount createUserAccount(String username, String rawPassword) {
//		if (userAccountRepository.findFirstByUsername(username).isPresent()) {
//			throw new IllegalArgumentException(
//					"UserAccount with username " + username + " already exists.");
//		}
		Optional<UserAccount> account = userAccountRepository.findFirstByUsername(username);
		UserAccount newUser = new UserAccount();
		if (account.isPresent()) {
			newUser = account.get();
		}
		newUser.setUsername(username);
		newUser.setPassword(passwordEncoder.encode(rawPassword)); // Hash the password
		List<Role> roles = new ArrayList<>();
		Optional<Role> role = roleRepository.findFirstByName("SUPER_ADMIN");
		if (role.isPresent()) {
			roles.add(role.get());
		}
		newUser.setRoles(roles);
		return userAccountRepository.save(newUser);
	}

	public void updatePassword(UserAccount UserAccount, String newRawPassword) {
		UserAccount.setPassword(passwordEncoder.encode(newRawPassword));
		userAccountRepository.save(UserAccount);
	}

	public boolean changePassword(String username, String oldRawPassword, String newRawPassword) {
		Optional<UserAccount> UserAccountOptional = findByUsername(username);
		if (UserAccountOptional.isEmpty()) {
			return false; // UserAccount not found
		}
		UserAccount UserAccount = UserAccountOptional.get();
		if (passwordEncoder.matches(oldRawPassword, UserAccount.getPassword())) {
			updatePassword(UserAccount, newRawPassword);
			return true;
		}
		return false; // Old password does not match
	}
}
