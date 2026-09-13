package com.yashpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yashpal.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {



}
