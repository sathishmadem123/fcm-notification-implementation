package com.fcm.service;

import com.fcm.dto.request.FcmRecipientDTO;
import com.fcm.dto.request.FcmSubscribeDTO;
import com.fcm.dto.request.FcmTopicDTO;
import com.fcm.dto.response.FcmRecipientResponseDTO;
import com.fcm.entity.FcmTopic;

import java.util.List;

public interface FcmTopicService {

    FcmTopicDTO save(FcmTopicDTO fcmTopicDTO);

    List<FcmTopicDTO> saveAll(List<FcmTopicDTO> fcmTopicDTOs);

    FcmTopicDTO getTopic(Long id);

    FcmTopicDTO getTopic(String name);

    FcmTopic getTopicEntity(Long id);

    FcmTopic getTopicEntity(String name);

    List<FcmTopicDTO> findAllTopics();

    void deleteTopic(List<String> names);

    void deleteTopic(Long id);

    void deleteTopic(FcmTopic fcmTopic);

    List<FcmRecipientResponseDTO> subscribeByRecipientId(List<FcmSubscribeDTO> dtos);

    List<FcmRecipientDTO> subscribeByToken(List<FcmSubscribeDTO> dtos);

    List<FcmRecipientResponseDTO> unsubscribeByRecipientId(List<FcmSubscribeDTO> dtos);

    List<FcmRecipientDTO> unsubscribeByToken(List<FcmSubscribeDTO> dtos);
}
