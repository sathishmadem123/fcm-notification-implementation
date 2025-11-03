package com.fcm.service.impl;

import com.fcm.dto.request.FcmRecipientDTO;
import com.fcm.dto.response.FcmRecipientResponseDTO;
import com.fcm.dto.response.TokenAndTopicDTO;
import com.fcm.entity.FcmRecipient;
import com.fcm.entity.FcmTopic;
import com.fcm.exception.RecordNotFoundException;
import com.fcm.repository.FcmRecipientRepository;
import com.fcm.repository.FcmTopicRepository;
import com.fcm.service.FcmRecipientService;
import com.fcm.service.FirebaseMessagingService;
import com.fcm.util.Constants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FcmRecipientServiceImpl implements FcmRecipientService {

    private final FcmRecipientRepository fcmRecipientRepository;
    private final FcmTopicRepository fcmTopicRepository;
    private final FirebaseMessagingService firebaseMessagingService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public FcmRecipientDTO save(FcmRecipientDTO dto) {
        FcmRecipient fcmRecipient = modelMapper.map(dto, FcmRecipient.class);

        fcmRecipient.setTopics(validateTopics(dto));

        FcmRecipient savedRecipient = fcmRecipientRepository.save(fcmRecipient);
        return modelMapper.map(savedRecipient, FcmRecipientDTO.class);
    }

    private List<FcmTopic> validateTopics(FcmRecipientDTO dto) {
        List<String> topics = dto.getTopics();
        List<Long> topicIds = dto.getTopicIds();

        if (topics != null && !topics.isEmpty()) {
            return topics.stream()
                    .map(topic -> {
                        FcmTopic fcmTopic = fcmTopicRepository.findByName(topic)
                                .orElseThrow(() -> new RecordNotFoundException(String.format(Constants.TOPIC_NOT_FOUND_MESSAGE, "name", topic)));
                        firebaseMessagingService.subscribe(dto.getToken(), fcmTopic.getName());

                        return fcmTopic;
                    }).toList();
        } else if (topicIds != null && !topicIds.isEmpty()) {
            return topicIds.stream()
                    .map(tId -> {
                        FcmTopic fcmTopic = fcmTopicRepository.findById(tId)
                                .orElseThrow(() -> new RecordNotFoundException(String.format(Constants.TOPIC_NOT_FOUND_MESSAGE, "id", tId)));
                        firebaseMessagingService.subscribe(dto.getToken(), fcmTopic.getName());

                        return fcmTopic;
                    }).toList();
        }
        return null;
    }

    @Override
    public List<FcmRecipientDTO> saveAll(List<FcmRecipientDTO> dtos) {
        List<FcmRecipient> recipients = dtos.stream()
                .map(dto -> {
                    FcmRecipient recipient = modelMapper.map(dto, FcmRecipient.class);
                    recipient.setTopics(validateTopics(dto));
                    return recipient;
                }).toList();

        return fcmRecipientRepository.saveAll(recipients).stream()
                .map(recipient -> modelMapper.map(recipient, FcmRecipientDTO.class))
                .toList();
    }

    @Override
    public List<FcmRecipient> saveAllRecipientEntities(List<FcmRecipient> dtos) {
        return fcmRecipientRepository.saveAll(dtos);
    }

    @Override
    public FcmRecipient save(FcmRecipient fcmRecipient) {
        return fcmRecipientRepository.save(fcmRecipient);
    }

    @Transactional(readOnly = true)
    @Override
    public FcmRecipientResponseDTO getByRecipientId(Long recipientId) {
        List<FcmRecipient> fcmRecipients = fcmRecipientRepository.findAllByRecipientId(recipientId);

        if (fcmRecipients == null || fcmRecipients.isEmpty()) {
            throw new RecordNotFoundException(String.format(Constants.RECIPIENT_NOT_FOUND_MESSAGE, recipientId));
        }

        return FcmRecipientResponseDTO.builder()
                .recipientId(fcmRecipients.get(0).getRecipientId())
                .tokens(getTokenAndTopicDTOS(fcmRecipients))
                .build();
    }

    @Override
    public FcmRecipient getByToken(String token) {
        return fcmRecipientRepository.findByToken(token).orElseThrow(() -> new RecordNotFoundException("Recipient not found with token: " + token));
    }

    @Override
    public List<TokenAndTopicDTO> getTokenAndTopicDTOS(List<FcmRecipient> fcmRecipients) {
        return fcmRecipients.stream()
                .map(fcmRecipient -> {
                    List<String> topicNames = fcmRecipient.getTopics().stream()
                            .map(FcmTopic::getName)
                            .toList();
                    return TokenAndTopicDTO.builder()
                            .token(fcmRecipient.getToken())
                            .topics(topicNames)
                            .build();
                }).toList();
    }

    @Override
    public List<FcmRecipientResponseDTO> getAll() {
        List<FcmRecipient> recipients = fcmRecipientRepository.findAll();

        Map<Long, List<FcmRecipient>> groupedRecipients = recipients.stream().collect(Collectors.groupingBy(FcmRecipient::getRecipientId));
        return groupedRecipients.entrySet().stream().map(entry -> {
            return FcmRecipientResponseDTO.builder()
                    .recipientId(entry.getKey())
                    .tokens(getTokenAndTopicDTOS(entry.getValue()))
                    .build();
        }).toList();
    }

    @Override
    public List<FcmRecipient> getRecipientEntity(Long id) {
        return fcmRecipientRepository.findAllByRecipientId(id);
    }

    @Transactional
    @Override
    public Map<String, Long> deleteByTokens(List<String> tokens) {

        long effectedRows = 0L;

        for (String token : tokens) {
            if (!fcmRecipientRepository.existsByToken(token))
                throw new RecordNotFoundException("Recipient not found with token: " + token);

            Long count = fcmRecipientRepository.deleteByToken(token);
            if (count != null && count != 0) {
                effectedRows++;
            }
        }
        firebaseMessagingService.unsubscribe(tokens);
        return Map.of("effectedRows", effectedRows);
    }

    @Transactional
    @Override
    public Map<String, Long> deleteByRecipientId(Long recipientId) {

        if (!fcmRecipientRepository.existsByRecipientId(recipientId))
            throw new RecordNotFoundException(String.format(Constants.RECIPIENT_NOT_FOUND_MESSAGE, recipientId));

        List<FcmRecipient> recipients = fcmRecipientRepository.findAllByRecipientId(recipientId);
        List<String> tokens = recipients.stream()
                .map(FcmRecipient::getToken).toList();
        firebaseMessagingService.unsubscribe(tokens);

        long effectedRows = fcmRecipientRepository.deleteByRecipientId(recipientId);
        return Map.of("effectedRows", effectedRows);
    }
}
