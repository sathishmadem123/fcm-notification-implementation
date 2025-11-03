package com.fcm.repository;

import com.fcm.entity.FcmRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmRecipientRepository extends JpaRepository<FcmRecipient, Long> {

    List<FcmRecipient> findAllByRecipientId(Long recipientId);

    Optional<FcmRecipient> findByToken(String token);

    boolean existsByRecipientId(Long recipientId);

    boolean existsByToken(String topics);

    Long deleteByToken(String token);

    Long deleteByRecipientId(Long recipientId);
}
