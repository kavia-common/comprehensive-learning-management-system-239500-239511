package com.example.lmsbackend.notify;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);
}
