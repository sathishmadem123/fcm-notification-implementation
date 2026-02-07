package com.fcm.service.impl;

import com.fcm.dto.request.FcmNotificationDTO;
import com.fcm.dto.request.FcmNotificationStatusDTO;
import com.fcm.entity.FcmRecipient;
import com.fcm.entity.FcmTopic;
import com.fcm.exception.FirebaseInternalException;
import com.fcm.exception.RecordNotFoundException;
import com.fcm.repository.FcmRecipientRepository;
import com.fcm.repository.FcmTopicRepository;
import com.fcm.service.FirebaseMessagingService;
import com.fcm.util.Constants;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirebaseMessagingServiceImpl implements FirebaseMessagingService {

    private final FirebaseMessaging firebaseMessaging;
    private final FcmTopicRepository fcmTopicRepository;
    private final FcmRecipientRepository fcmRecipientRepository;

    @Override
    public void subscribe(String token, String topicName) {
        try {
            firebaseMessaging.subscribeToTopic(List.of(token), topicName);
        } catch (FirebaseMessagingException e) {
            throw new FirebaseInternalException(e.getMessage());
        }
    }

    @Override
    public void subscribe(String token, List<String> topicNames) {
        topicNames.forEach(topic -> {
            try {
                firebaseMessaging.subscribeToTopic(List.of(token), topic);
            } catch (FirebaseMessagingException e) {
                throw new FirebaseInternalException(e.getMessage());
            }
        });
    }

    @Override
    public void subscribe(List<String> tokens, List<String> topics) {
        topics.forEach(topic -> {
            try {
                firebaseMessaging.subscribeToTopic(tokens, topic);
            } catch (FirebaseMessagingException e) {
                throw new FirebaseInternalException(e.getMessage());
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
                throw new FirebaseInternalException(e.getMessage());
            }
        });
    }

    @Override
    public void unsubscribe(String token, List<String> topicNames) {
        topicNames.forEach(topic -> {
            try {
                firebaseMessaging.unsubscribeFromTopic(List.of(token), topic);
            } catch (FirebaseMessagingException e) {
                throw new FirebaseInternalException(e.getMessage());
            }
        });
    }

    @Override
    public void unsubscribe(List<String> tokens, String topicName) {
        try {
            firebaseMessaging.unsubscribeFromTopic(tokens, topicName);
        } catch (FirebaseMessagingException e) {
            throw new FirebaseInternalException(e.getMessage());
        }
    }

    @Override
    public void unsubscribe(List<String> tokens, List<String> topics) {
        topics.forEach(topic -> {
            try {
                firebaseMessaging.unsubscribeFromTopic(tokens, topic);
            } catch (FirebaseMessagingException e) {
                throw new FirebaseInternalException(e.getMessage());
            }
        });
    }

    @Override
    public Map<String, Object> sendNotificationByToken(FcmNotificationDTO fcmNotificationDTO) {

        List<Message> messages = new ArrayList<>();
        Notification notification = getNotification(fcmNotificationDTO);
        fcmNotificationDTO.getTokens().forEach(token -> {
            messages.add(Message.builder()
                    .setNotification(notification)
                    .setToken(token)
                    .build()
            );
        });

        try {
            BatchResponse batchResponse = firebaseMessaging.sendEach(messages);
            Map<String, Object> response = new HashMap<>();
            response.put("responses", decodeBatchResponse(batchResponse));
            return response;
        } catch (FirebaseMessagingException e) {
            throw new FirebaseInternalException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> sendByRecipientId(FcmNotificationDTO fcmNotificationDTO) {
        List<FcmRecipient> fcmRecipients = fcmRecipientRepository.findAllByRecipientId(fcmNotificationDTO.getRecipientId());
        if (fcmRecipients.isEmpty())
            throw new RecordNotFoundException(String.format(Constants.RECIPIENT_NOT_FOUND_MESSAGE, fcmNotificationDTO.getRecipientId()));

        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(getNotification(fcmNotificationDTO))
                .putAllData(fcmNotificationDTO.getData())
                .addAllTokens(getTokens(fcmRecipients))
                .build();

        try {
            BatchResponse batchResponse = firebaseMessaging.sendEachForMulticast(multicastMessage);
            Map<String, Object> response = new HashMap<>();
            response.put("responses", decodeBatchResponse(batchResponse));
            return response;
        } catch (FirebaseMessagingException e) {
            throw new FirebaseInternalException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> sendByTopic(FcmNotificationDTO fcmNotificationDTO) {
        List<String> topics = fcmNotificationDTO.getTopics();
        topics.forEach(topic -> {
            if (!fcmTopicRepository.existsByName(topic)) {
                throw new RecordNotFoundException(String.format(Constants.TOPIC_NOT_FOUND_MESSAGE, "name", topic));
            }
        });

        Notification notification = getNotification(fcmNotificationDTO);
        List<Message> messages = topics.stream().map(topic -> Message.builder()
                .setTopic(topic)
                .setNotification(notification)
                .putAllData(fcmNotificationDTO.getData())
                .build()).toList();

        try {
            BatchResponse batchResponse = firebaseMessaging.sendEach(messages);
            Map<String, Object> response = new HashMap<>();
            response.put("responses", decodeBatchResponse(batchResponse));
            return response;
        } catch (FirebaseMessagingException e) {
            throw new FirebaseInternalException(e.getMessage());
        }
    }

    private Notification getNotification(FcmNotificationDTO fcmNotificationDTO) {
        return fcmNotificationDTO.isNotificationRequired() ? Notification.builder()
                .setTitle(fcmNotificationDTO.getTitle())
                .setBody(fcmNotificationDTO.getBody())
                .setImage(fcmNotificationDTO.getImage())
                .build() : null;
    }

    private List<FcmNotificationStatusDTO> decodeBatchResponse(BatchResponse batchResponse) {
        List<FcmNotificationStatusDTO> responses = new ArrayList<>();
        batchResponse.getResponses().forEach(r -> {
            if (r.isSuccessful()) {
                responses.add(FcmNotificationStatusDTO.builder()
                        .sent(true)
                        .messageId(r.getMessageId())
                        .build());
            } else {
                responses.add(FcmNotificationStatusDTO.builder()
                        .sent(false)
                        .eMessage(r.getException().getMessage())
                        .build());
            }
        });
        return responses;
    }

    private List<String> getTokens(List<FcmRecipient> fcmRecipients) {
        return fcmRecipients.stream().map(FcmRecipient::getToken).toList();
    }
}
