package com.fcm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    MALFORMED_JSON(1000, "Malformed JSON"),
    NOT_FOUND(1001, "Resource Not Found"),
    SQL_EXCEPTION(1002, "SQL Query Exception"),
    FIREBASE_EXCEPTION(1003, "Firebase Messaging Exception");

    private final int eCode;
    private final String eMessage;
}
