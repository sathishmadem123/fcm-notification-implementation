package com.fcm.service.impl;

import com.fcm.dto.request.FcmRecipientDTO;
import com.fcm.dto.request.FcmSubscribeDTO;
import com.fcm.dto.request.FcmTopicDTO;
import com.fcm.dto.response.FcmRecipientResponseDTO;
import com.fcm.entity.FcmRecipient;
import com.fcm.entity.FcmTopic;
import com.fcm.exception.RecordNotFoundException;
import com.fcm.repository.FcmTopicRepository;
import com.fcm.service.FcmRecipientService;
import com.fcm.service.FcmTopicService;
import com.fcm.service.FirebaseMessagingService;
import com.fcm.util.Constants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmTopicServiceImpl implements FcmTopicService {

    private final FcmTopicRepository fcmTopicRepository;
    private final FirebaseMessagingService firebaseMessagingService;
    private final FcmRecipientService fcmRecipientService;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public FcmTopicDTO save(final FcmTopicDTO fcmTopicDTO) {

        final FcmTopic inputTopic = modelMapper.map(fcmTopicDTO, FcmTopic.class);
        final FcmTopic responseTopic = fcmTopicRepository.save(inputTopic);
        return modelMapper.map(responseTopic, FcmTopicDTO.class);
    }

    @Transactional
    @Override
    public List<FcmTopicDTO> saveAll(final List<FcmTopicDTO> fcmTopicDTOS) {

        final List<FcmTopic> inputTopics = fcmTopicDTOS.stream()
                .map(dto -> modelMapper.map(dto, FcmTopic.class))
                .toList();

        final List<FcmTopic> savedTopics = fcmTopicRepository.saveAll(inputTopics);

        return savedTopics.stream()
                .map(e -> modelMapper.map(e, FcmTopicDTO.class))
                .toList();
    }

    @Override
    public FcmTopicDTO getTopic(Long id) {
        FcmTopic fcmTopic = fcmTopicRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Fcm Topic is not found with id " + id));
        return modelMapper.map(fcmTopic, FcmTopicDTO.class);
    }

    @Override
    public List<FcmTopicDTO> findAllTopics() {
        List<FcmTopic> topics = fcmTopicRepository.findAll();

        return topics.stream()
                .map(t -> modelMapper.map(t, FcmTopicDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public void deleteTopic(List<String> names) {

        for (String name : names) {

            FcmTopic topic = getTopicEntity(name);
            List<FcmRecipient> recipients = topic.getRecipients();

            List<String> tokens = recipients.stream()
                    .map(FcmRecipient::getToken)
                    .toList();

            if (!tokens.isEmpty()) {
                firebaseMessagingService.unsubscribe(tokens, name);
            }

            recipients.forEach(recipient -> {
                recipient.getTopics().remove(topic);
                fcmRecipientService.save(recipient);
            });

            deleteTopic(topic);
        }
    }

    @Override
    public void deleteTopic(Long id) {
        FcmTopic topic = getTopicEntity(id);
        List<FcmRecipient> recipients = topic.getRecipients();

        List<String> tokens = recipients.stream()
                .map(FcmRecipient::getToken)
                .toList();

        if (!tokens.isEmpty()) {
            firebaseMessagingService.unsubscribe(tokens, topic.getName());
        }

        recipients.forEach(recipient -> {
            recipient.getTopics().remove(topic);
            fcmRecipientService.save(recipient);
        });

        deleteTopic(topic);
    }

    @Override
    public void deleteTopic(FcmTopic fcmTopic) {
        fcmTopicRepository.delete(fcmTopic);
    }

    @Override
    public FcmTopicDTO getTopic(String name) {
        FcmTopic topic = fcmTopicRepository.findByName(name)
                .orElseThrow(() -> new RecordNotFoundException(String.format(Constants.TOPIC_NOT_FOUND_MESSAGE, "name", name)));

        return modelMapper.map(topic, FcmTopicDTO.class);
    }

    @Override
    public FcmTopic getTopicEntity(Long id) {
        return fcmTopicRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(String.format(Constants.TOPIC_NOT_FOUND_MESSAGE, "id", id)));
    }

    @Override
    public FcmTopic getTopicEntity(String name) {
        return fcmTopicRepository.findByName(name)
                .orElseThrow(() -> new RecordNotFoundException(String.format(Constants.TOPIC_NOT_FOUND_MESSAGE, "name", name)));
    }

    @Override
    @Transactional
    public List<FcmRecipientResponseDTO> subscribeByRecipientId(List<FcmSubscribeDTO> dtos) {
        List<List<FcmRecipient>> savedRecipients = dtos.stream().map(dto -> {
            List<FcmTopic> topics = dto.getTopics().stream().map(this::getTopicEntity).toList();
            List<FcmRecipient> recipients = fcmRecipientService.getRecipientEntity(dto.getRecipientId());

            List<String> tokens = recipients.stream().map(FcmRecipient::getToken).toList();
            firebaseMessagingService.subscribe(tokens, dto.getTopics());

            List<FcmRecipient> updatedRecipients = recipients.stream().map(recipient -> {
                recipient.getTopics().addAll(topics);
                return recipient;
            }).toList();

            return fcmRecipientService.saveAllRecipientEntities(updatedRecipients);
        }).toList();

        return savedRecipients.stream().map(recipient -> FcmRecipientResponseDTO.builder()
                .recipientId(recipient.get(0).getRecipientId())
                .tokens(fcmRecipientService.getTokenAndTopicDTOS(recipient))
                .build()).toList();

    }

    @Override
    public List<FcmRecipientDTO> subscribeByToken(List<FcmSubscribeDTO> dtos) {

        List<FcmRecipient> updatedRecipients = dtos.stream().map(dto -> {
            FcmRecipient recipient = fcmRecipientService.getByToken(dto.getToken());
            List<FcmTopic> topics = dto.getTopics().stream().map(this::getTopicEntity).toList();

            firebaseMessagingService.subscribe(recipient.getToken(), dto.getTopics());

            recipient.getTopics().addAll(topics);
            return recipient;
        }).toList();

        List<FcmRecipient> savedRecipients = fcmRecipientService.saveAllRecipientEntities(updatedRecipients);

        return savedRecipients.stream().map(recipient -> FcmRecipientDTO.builder()
                .recipientId(recipient.getRecipientId())
                .token(recipient.getToken())
                .topics(recipient.getTopics().stream().map(FcmTopic::getName).toList()).build()).toList();
    }

    @Override
    public List<FcmRecipientResponseDTO> unsubscribeByRecipientId(List<FcmSubscribeDTO> dtos) {
        List<List<FcmRecipient>> lstRecipients = dtos.stream().map(dto -> {
            List<FcmRecipient> recipients = fcmRecipientService.getRecipientEntity(dto.getRecipientId());
            List<FcmTopic> topics = dto.getTopics().stream().map(this::getTopicEntity).toList();

            List<String> tokens = recipients.stream().map(FcmRecipient::getToken).toList();
            firebaseMessagingService.unsubscribe(tokens, dto.getTopics());

            List<FcmRecipient> updatedRecipients = recipients.stream().map(recipient -> {
                recipient.getTopics().removeAll(topics);
                return recipient;
            }).toList();

            return fcmRecipientService.saveAllRecipientEntities(updatedRecipients);
        }).toList();

        return lstRecipients.stream().map(recipient -> FcmRecipientResponseDTO.builder()
                .recipientId(recipient.get(0).getRecipientId())
                .tokens(fcmRecipientService.getTokenAndTopicDTOS(recipient)).build()).toList();
    }

    @Override
    public List<FcmRecipientDTO> unsubscribeByToken(List<FcmSubscribeDTO> dtos) {

        List<FcmRecipient> updatedRecipients = dtos.stream().map(dto -> {
            FcmRecipient recipient = fcmRecipientService.getByToken(dto.getToken());
            List<FcmTopic> topics = dto.getTopics().stream().map(this::getTopicEntity).toList();

            firebaseMessagingService.unsubscribe(dto.getToken(), dto.getTopics());

            recipient.getTopics().removeAll(topics);
            return recipient;
        }).toList();

        List<FcmRecipient> savedRecipients = fcmRecipientService.saveAllRecipientEntities(updatedRecipients);

        return savedRecipients.stream().map(recipient -> FcmRecipientDTO.builder()
                .recipientId(recipient.getRecipientId())
                .token(recipient.getToken())
                .topics(recipient.getTopics().stream().map(FcmTopic::getName).toList()).build()).toList();
    }
}
