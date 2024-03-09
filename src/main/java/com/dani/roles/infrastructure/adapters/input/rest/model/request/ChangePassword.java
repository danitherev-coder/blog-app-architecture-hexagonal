package com.dani.roles.infrastructure.adapters.input.rest.model.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePassword {
    @NotBlank(message = "La contraseña no puede estar vacia")
    private String password;
}
