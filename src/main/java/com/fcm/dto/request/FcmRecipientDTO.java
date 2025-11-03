package com.fcm.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FcmRecipientDTO {

    @NotNull(message = "recipientId is missing or it's value can't be null or empty")
    private Long recipientId;

    @NotBlank(message = "token is missing or it's value mustn't be null or empty")
    private String token;

    private List<String> topics;
    private List<Long> topicIds;
}
