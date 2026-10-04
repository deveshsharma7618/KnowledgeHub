package com.deveshsharma.knowledgehub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@AllArgsConstructor
@Getter
public class ErrorResponse {
    String title;
    int status;
    String detail;
}
