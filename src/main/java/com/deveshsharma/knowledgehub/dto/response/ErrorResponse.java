package com.deveshsharma.knowledgehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@AllArgsConstructor
@Getter
/**
 * Standard error payload returned by the REST exception handler.
 */
public class ErrorResponse {
    String title;
    int status;
    String detail;
}
