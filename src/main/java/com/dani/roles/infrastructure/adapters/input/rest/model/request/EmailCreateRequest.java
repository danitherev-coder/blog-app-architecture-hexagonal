package com.dani.roles.infrastructure.adapters.input.rest.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCreateRequest {
    @NotBlank(message = "El nombre de usuario no puede estar vacio")
    private String email;
}
