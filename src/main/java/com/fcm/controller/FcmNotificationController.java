package com.fcm.controller;

import com.fcm.dto.request.FcmNotificationDTO;
import com.fcm.dto.response.ApiResponse;
import com.fcm.service.FirebaseMessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/fcm-notification", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FcmNotificationController {

    private final FirebaseMessagingService firebaseMessagingService;

    @PostMapping(value = "/sendByToken")
    public ApiResponse sendByToken(@RequestBody FcmNotificationDTO fcmNotificationDTO) {
        return new ApiResponse(true, "Notification sent successfully", firebaseMessagingService.sendNotificationByToken(fcmNotificationDTO));
    }

    @PostMapping(value = "/sendByRecipientId")
    public ApiResponse sendByRecipientId(@RequestBody FcmNotificationDTO fcmNotificationDTO) {
        return new ApiResponse(true, "Notifications sent successfully", firebaseMessagingService.sendByRecipientId(fcmNotificationDTO));
    }

    @PostMapping(value = "/sendByTopic")
    public ApiResponse sendByTopic(@RequestBody FcmNotificationDTO fcmNotificationDTO) {
        return new ApiResponse(true, "Notifications sent successfully", firebaseMessagingService.sendByTopic(fcmNotificationDTO));
    }
}
