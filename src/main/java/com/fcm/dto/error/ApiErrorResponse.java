package com.fcm.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fcm.enums.ErrorCode;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiErrorResponse {

    private Instant timestamp;
    private boolean success;
    private int status;
    private ErrorCode error;
    private String cause;
    private int eCode;
    private String message;
    private List<String> details;
    private String debugInformation;
    private String path;
}
