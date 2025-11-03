package com.fcm.controller;

import com.fcm.dto.request.FcmRecipientDTO;
import com.fcm.dto.response.ApiResponse;
import com.fcm.dto.response.FcmRecipientResponseDTO;
import com.fcm.service.FcmRecipientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/fcm-recipient", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FcmRecipientController {

    private final FcmRecipientService fcmRecipientService;

    @PostMapping("/add")
    public ApiResponse addRecipient(@RequestBody @Valid FcmRecipientDTO dto) {
        FcmRecipientDTO responseDTO = fcmRecipientService.save(dto);
        return new ApiResponse(true, "Recipient saved Successfully!", responseDTO);
    }

    @PostMapping("/addAll")
    public ApiResponse addAllRecipients(@RequestBody @Valid List<FcmRecipientDTO> dtos) {
        List<FcmRecipientDTO> responseDTO = fcmRecipientService.saveAll(dtos);
        return new ApiResponse(true, "Recipient saved Successfully!", responseDTO);
    }

    @GetMapping(value = "/getById/{recipientId}")
    public ApiResponse getRecipient(@PathVariable("recipientId") Long recipientId) {
        FcmRecipientResponseDTO responseDTO = fcmRecipientService.getByRecipientId(recipientId);
        return new ApiResponse(true, "Recipient fetched Successfully!", responseDTO);
    }

    @GetMapping(value = "/getAll")
    public ApiResponse getAllRecipients() {
        List<FcmRecipientResponseDTO> responseDTOs = fcmRecipientService.getAll();
        return new ApiResponse(true, "Recipients fetched Successfully!", responseDTOs);
    }

    @DeleteMapping(value = "/deleteTokens")
    public ApiResponse deleteRecipientToken(@RequestParam List<String> tokens) {
        Map<String, Long> responseBody = fcmRecipientService.deleteByTokens(tokens);
        return new ApiResponse(true, "Tokens deleted successfully", responseBody);
    }

    @DeleteMapping(value = "/deleteById/{recipientId}")
    public ApiResponse deleteById(@PathVariable("recipientId") Long recipientId) {
        Map<String, Long> responseBody = fcmRecipientService.deleteByRecipientId(recipientId);
        return new ApiResponse(true, "Tokens deleted successfully", responseBody);
    }
}
