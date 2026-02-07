package com.fcm.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FcmNotificationStatusDTO {

    private boolean sent;
    private String messageId;
    private String eMessage;
}
