package com.fcm.repository;

import com.fcm.entity.FcmTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTopicRepository extends JpaRepository<FcmTopic, Long> {

    Optional<FcmTopic> findByName(String name);
}
