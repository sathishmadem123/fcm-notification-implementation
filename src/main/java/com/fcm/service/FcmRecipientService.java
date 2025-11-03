package com.fcm.service;

import com.fcm.dto.request.FcmRecipientDTO;
import com.fcm.dto.response.FcmRecipientResponseDTO;
import com.fcm.dto.response.TokenAndTopicDTO;
import com.fcm.entity.FcmRecipient;

import java.util.List;
import java.util.Map;

public interface FcmRecipientService {

    FcmRecipientDTO save(FcmRecipientDTO dto);

    FcmRecipient save(FcmRecipient fcmRecipient);

    List<FcmRecipientDTO> saveAll(List<FcmRecipientDTO> dtos);

    List<FcmRecipient> saveAllRecipientEntities(List<FcmRecipient> dtos);

    FcmRecipientResponseDTO getByRecipientId(Long id);

    List<FcmRecipient> getRecipientEntity(Long id);

    FcmRecipient getByToken(String token);

    List<FcmRecipientResponseDTO> getAll();

    Map<String, Long> deleteByTokens(List<String> tokens);

    Map<String, Long> deleteByRecipientId(Long recipientId);

    List<TokenAndTopicDTO> getTokenAndTopicDTOS(List<FcmRecipient> fcmRecipients);
}
