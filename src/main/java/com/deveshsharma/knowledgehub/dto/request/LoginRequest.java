package com.deveshsharma.knowledgehub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class LoginRequest {
    @Email
    private String email;

    @NotEmpty
    private String password;
}
