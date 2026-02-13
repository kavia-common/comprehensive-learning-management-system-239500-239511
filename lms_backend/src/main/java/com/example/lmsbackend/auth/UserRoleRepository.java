package com.example.lmsbackend.auth;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRoleRepository {

	private final JdbcTemplate jdbcTemplate;

	public UserRoleRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void addRoleToUser(long userId, long roleId) {
		// Postgres requires an explicit conflict target when using ON CONFLICT with a composite PK.
		jdbcTemplate.update(
				"INSERT INTO user_roles(user_id, role_id) VALUES (?, ?) ON CONFLICT (user_id, role_id) DO NOTHING",
				userId, roleId
		);
	}

	public void removeAllRolesForUser(long userId) {
		jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", userId);
	}

	public List<String> findRoleNamesByUserId(long userId) {
		return jdbcTemplate.query(
				"SELECT r.name FROM roles r JOIN user_roles ur ON ur.role_id = r.id WHERE ur.user_id = ?",
				(rs, rowNum) -> rs.getString(1),
				userId
		);
	}
}
