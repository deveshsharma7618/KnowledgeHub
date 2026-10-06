package com.deveshsharma.knowledgehub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserRequest {
    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;
}
