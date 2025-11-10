package com.malugu.springboot_starter_project.config.auth;

import com.malugu.springboot_starter_project.uaa.entity.UserAccount;
import com.malugu.springboot_starter_project.uaa.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserAccountRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserAccount> userOptional = userRepository.findFirstByUsername(username);
		if (userOptional.isPresent()) {
			UserAccount user = userOptional.get();
			String authorities[] = user.getAuthorities() != null && !user.getAuthorities().isEmpty()
					? getAuthorities(user)
					: new String[0];
			return User.builder().username(user.getUsername())
					.password(user.getPassword()).roles(authorities).build();
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}

	private String[] getAuthorities(UserAccount user) {
		List<String> authorities = new ArrayList<>();
		for (GrantedAuthority authority : user.getAuthorities()) {
			authorities.add(authority.getAuthority());
		}
		String[] stringArray = authorities.toArray(new String[0]);
		return stringArray;
	}
}
