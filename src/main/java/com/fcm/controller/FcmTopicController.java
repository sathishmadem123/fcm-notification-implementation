package com.fcm.controller;

import com.fcm.dto.request.FcmRecipientDTO;
import com.fcm.dto.request.FcmSubscribeDTO;
import com.fcm.dto.request.FcmTopicDTO;
import com.fcm.dto.response.ApiResponse;
import com.fcm.dto.response.FcmRecipientResponseDTO;
import com.fcm.service.FcmTopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/fcm-topic", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FcmTopicController {

    private final FcmTopicService fcmTopicService;

    @PostMapping("/add")
    public ApiResponse addTopic(@RequestBody @Valid FcmTopicDTO fcmTopicDTO) {
        FcmTopicDTO savedTopic = fcmTopicService.save(fcmTopicDTO);

        return new ApiResponse(true, "Topic saved successfully!", savedTopic);
    }

    @PostMapping("/addAll")
    public ApiResponse addAllTopics(@RequestBody @Valid List<FcmTopicDTO> fcmTopicDTOS) {
        List<FcmTopicDTO> savedTopics = fcmTopicService.saveAll(fcmTopicDTOS);

        return new ApiResponse(true, "Topics saved successfully!", savedTopics);
    }

    @GetMapping("/getById/{id}")
    public ApiResponse getById(@PathVariable("id") Long id) {
        FcmTopicDTO fcmTopicDTO = fcmTopicService.getTopic(id);
        return new ApiResponse(true, "Topic fetched successfully!", fcmTopicDTO);
    }

    @GetMapping("/getByName/{name}")
    public ApiResponse getByName(@PathVariable("name") String name) {
        FcmTopicDTO fcmTopicDTO = fcmTopicService.getTopic(name);
        return new ApiResponse(true, "Topic fetched successfully!", fcmTopicDTO);
    }

    @GetMapping("/getAll")
    public ApiResponse getAll() {
        List<FcmTopicDTO> topics = fcmTopicService.findAllTopics();
        return new ApiResponse(true, "Topics fetched successfully!", topics);
    }

    @DeleteMapping("/deleteByName")
    public ApiResponse deleteTopicByName(@RequestParam List<String> names) {
        fcmTopicService.deleteTopic(names);
        return new ApiResponse(true, "Deleted successfully", null);
    }

    @DeleteMapping("/deleteById/{id}")
    public ApiResponse deleteTopicById(@PathVariable("id") Long id) {
        fcmTopicService.deleteTopic(id);
        return new ApiResponse(true, "Deleted successfully", null);
    }

    @PostMapping("/subscribeByRecipientId")
    public ApiResponse subscribeByRecipientId(@RequestBody List<FcmSubscribeDTO> dtos) {
        List<FcmRecipientResponseDTO> responseDTOS = fcmTopicService.subscribeByRecipientId(dtos);
        return new ApiResponse(true, "Subscribed successfully", responseDTOS);
    }

    @PostMapping("/subscribeByToken")
    public ApiResponse subscribeByToken(@RequestBody List<FcmSubscribeDTO> dtos) {
        List<FcmRecipientDTO> responseDTOS = fcmTopicService.subscribeByToken(dtos);
        return new ApiResponse(true, "Subscribed successfully", responseDTOS);
    }

    @PostMapping("/unsubscribeByRecipientId")
    public ApiResponse unsubscribeByRecipientId(@RequestBody List<FcmSubscribeDTO> dtos) {
        List<FcmRecipientResponseDTO> responseDTOS = fcmTopicService.unsubscribeByRecipientId(dtos);
        return new ApiResponse(true, "Unsubscribed successfully", responseDTOS);
    }

    @PostMapping("/unsubscribeByToken")
    public ApiResponse unsubscribeByToken(@RequestBody List<FcmSubscribeDTO> dtos) {
        List<FcmRecipientDTO> responseDTOS = fcmTopicService.unsubscribeByToken(dtos);
        return new ApiResponse(true, "Unsubscribed successfully", responseDTOS);
    }
}
