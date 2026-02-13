package com.example.lmsbackend.auth;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

	public enum Name {
		ADMIN,
		INSTRUCTOR,
		STUDENT
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, unique = true, length = 50)
	private Name name;

	protected Role() {
	}

	public Role(Name name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public Name getName() {
		return name;
	}
}
