package com.malugu.springboot_starter_project.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable method security
public class SecurityConfig {

	private final UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	@Lazy
	private CustomAuthenticationProvider authenticationProvider;

	public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl) {
		this.userDetailsServiceImpl = userDetailsServiceImpl;
	}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
						.requestMatchers("/auth/login", "/auth/refresh-token", "/auth/forgot-password",
								"/auth/reset-password", "/sandbox", "/sandbox.html", "/kiwiko",
								"/ufukweni")
						.permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer((oauth2) -> oauth2
                        .opaqueToken(withDefaults())
                )
                .sessionManagement((session) -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .csrf((csrf) -> csrf.disable());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
		return this.userDetailsServiceImpl;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); // In production use BCryptPasswordEncoder
    }

    @Bean
	public AuthenticationManager authenticationManager() {
//		CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider();
//		authenticationProvider.setUserDetailsService(userDetailsService());
//		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return new ProviderManager(authenticationProvider);
	}

//	@Bean
//    public OpaqueTokenIntrospector introspector() {
//		return new DatabaseIntrospector(tokenRepository);
//    }
}
