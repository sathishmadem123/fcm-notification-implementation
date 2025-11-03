package com.fcm.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FcmRecipientResponseDTO {

    private Long recipientId;
    private List<TokenAndTopicDTO> tokens;
    private TokenAndTopicDTO token;
}
