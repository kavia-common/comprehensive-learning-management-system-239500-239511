package com.example.lmsbackend;

import com.example.lmsbackend.auth.Role;
import com.example.lmsbackend.auth.RoleRepository;
import com.example.lmsbackend.auth.User;
import com.example.lmsbackend.auth.UserRepository;
import com.example.lmsbackend.auth.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

/**
 * Application entrypoint for the LMS backend.
 */
@SpringBootApplication
public class LmsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmsBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner seedAdmin(RoleRepository roleRepository, UserRepository userRepository, UserRoleRepository userRoleRepository) {
		return args -> {
			for (Role.Name n : Role.Name.values()) {
				roleRepository.findByName(n).orElseGet(() -> roleRepository.save(new Role(n)));
			}

			String adminEmail = System.getenv("LMS_BOOTSTRAP_ADMIN_EMAIL");
			String adminPassword = System.getenv("LMS_BOOTSTRAP_ADMIN_PASSWORD");
			String adminName = Optional.ofNullable(System.getenv("LMS_BOOTSTRAP_ADMIN_NAME")).orElse("Admin");
			if (adminEmail == null || adminPassword == null) {
				return;
			}
			if (userRepository.findByEmail(adminEmail).isPresent()) {
				return;
			}

			org.springframework.security.crypto.password.PasswordEncoder encoder =
					new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

			User admin = new User(adminEmail, encoder.encode(adminPassword), adminName);
			admin = userRepository.save(admin);

			Role adminRole = roleRepository.findByName(Role.Name.ADMIN).orElseThrow();
			userRoleRepository.addRoleToUser(admin.getId(), adminRole.getId());
		};
	}
}
