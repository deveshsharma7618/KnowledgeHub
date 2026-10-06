package com.deveshsharma.knowledgehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Standard error payload returned by the REST exception handler.
 */
@Setter
@AllArgsConstructor
@Getter
public class ErrorResponse {
    String title;
    int status;
    String detail;
}
