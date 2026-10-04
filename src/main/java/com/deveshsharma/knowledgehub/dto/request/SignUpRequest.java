package com.deveshsharma.knowledgehub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

/**
 * User details submitted to the registration endpoint.
 */
@Getter
public class SignUpRequest {
    @NotEmpty(
            message = "Name can't be empty."
    )
    String name;

    @Email(
            message = "Invalid email."
    )
    String email;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password must contain at least one letter, one number, and one special character, and be 8+ characters long."
    )
    String password;
}
