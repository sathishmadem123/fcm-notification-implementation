package com.fcm.service;

import com.fcm.dto.request.FcmNotificationDTO;

import java.util.List;
import java.util.Map;

public interface FirebaseMessagingService {

    void subscribe(String token, String topicName);

    void subscribe(String token, List<String> topicNames);

    void subscribe(List<String> tokens, List<String> topics);

    void unsubscribe(List<String> tokens);

    void unsubscribe(String token, List<String> topicNames);

    void unsubscribe(List<String> tokens, String topicName);

    void unsubscribe(List<String> tokens, List<String> topics);

    Map<String, Object> sendNotificationByToken(FcmNotificationDTO fcmNotificationDTO);

    Map<String, Object> sendByRecipientId(FcmNotificationDTO fcmNotificationDTO);

    Map<String, Object> sendByTopic(FcmNotificationDTO fcmNotificationDTO);
}
