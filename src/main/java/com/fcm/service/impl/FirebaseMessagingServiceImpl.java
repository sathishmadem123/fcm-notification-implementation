package com.fcm.service.impl;

import com.fcm.entity.FcmTopic;
import com.fcm.repository.FcmTopicRepository;
import com.fcm.service.FirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirebaseMessagingServiceImpl implements FirebaseMessagingService {

    private final FirebaseMessaging firebaseMessaging;
    private final FcmTopicRepository fcmTopicRepository;

    @Override
    public void subscribe(String token, String topicName) {
        try {
            firebaseMessaging.subscribeToTopic(List.of(token), topicName);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(String token, List<String> topicNames) {
        topicNames.forEach(topic -> {
            try {
                firebaseMessaging.subscribeToTopic(List.of(token), topic);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void subscribe(List<String> tokens, List<String> topics) {
        topics.forEach(topic -> {
            try {
                firebaseMessaging.subscribeToTopic(tokens, topic);
            } catch (FirebaseMessagingException e) {
                log.info(Arrays.toString(e.getStackTrace()));
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void unsubscribe(List<String> tokens) {
        List<FcmTopic> topics = fcmTopicRepository.findAll();
        topics.forEach(topic -> {
            try {
                firebaseMessaging.unsubscribeFromTopic(tokens, topic.getName());
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void unsubscribe(String token, List<String> topicNames) {
        topicNames.forEach(topic -> {
            try {
                firebaseMessaging.unsubscribeFromTopic(List.of(token), topic);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void unsubscribe(List<String> tokens, String topicName) {
        try {
            firebaseMessaging.unsubscribeFromTopic(tokens, topicName);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unsubscribe(List<String> tokens, List<String> topics) {
        topics.forEach(topic -> {
            try {
                firebaseMessaging.unsubscribeFromTopic(tokens, topic);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
