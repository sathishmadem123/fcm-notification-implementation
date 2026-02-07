package com.fcm.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FcmNotificationDTO {

    private Long recipientId;
    private String title;
    private String body;
    private String image;
    private Map<String, String> data;
    private List<String> tokens;
    private List<String> topics;
    private boolean notificationRequired;
}
