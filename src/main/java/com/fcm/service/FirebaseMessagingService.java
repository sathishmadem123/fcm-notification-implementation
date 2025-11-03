package com.fcm.service;

import java.util.List;

public interface FirebaseMessagingService {

    void subscribe(String token, String topicName);

    void subscribe(String token, List<String> topicNames);

    void subscribe(List<String> tokens, List<String> topics);

    void unsubscribe(List<String> tokens);

    void unsubscribe(String token, List<String> topicNames);

    void unsubscribe(List<String> tokens, String topicName);

    void unsubscribe(List<String> tokens, List<String> topics);
}
