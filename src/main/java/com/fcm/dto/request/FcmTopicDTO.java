package com.fcm.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FcmTopicDTO {

    private Long id;

    @NotBlank(message = "Topic name mustn't be null or empty")
    private String name;

    @NotBlank(message = "Description mustn't be null or empty")
    private String description;
}
