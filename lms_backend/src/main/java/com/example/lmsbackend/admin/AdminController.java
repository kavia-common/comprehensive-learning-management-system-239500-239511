package com.example.lmsbackend.admin;

import com.example.lmsbackend.auth.Role;
import com.example.lmsbackend.auth.RoleRepository;
import com.example.lmsbackend.auth.UserRepository;
import com.example.lmsbackend.auth.UserRoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserRoleRepository userRoleRepository;

	public AdminController(UserRepository userRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userRoleRepository = userRoleRepository;
	}

	public record SetGlobalRoleRequest(@NotNull Long userId, @NotBlank String role) {
	}

	@PostMapping("/users/role")
	@Operation(summary = "Set global role", description = "Assigns a global role (ADMIN/INSTRUCTOR/STUDENT) to a user. Replaces existing global roles.")
	public void setRole(@RequestBody SetGlobalRoleRequest req) {
		userRepository.findById(req.userId()).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found"));
		Role.Name roleName = Role.Name.valueOf(req.role().toUpperCase());
		Role r = roleRepository.findByName(roleName).orElseThrow();
		userRoleRepository.removeAllRolesForUser(req.userId());
		userRoleRepository.addRoleToUser(req.userId(), r.getId());
	}
}
