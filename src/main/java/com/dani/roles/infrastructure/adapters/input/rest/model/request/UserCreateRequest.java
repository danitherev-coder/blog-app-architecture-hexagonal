package com.dani.roles.infrastructure.adapters.input.rest.model.request;

import com.dani.roles.domain.model.Role;
import com.dani.roles.infrastructure.adapters.output.persistence.entities.RoleEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    @NotBlank(message = "EL nombre no puede estar vacio")
    private String firstname;
    @NotBlank(message = "EL apellido no puede estar vacio")
    private String lastname;
    @NotBlank(message = "El nombre de usuario no puede estar vacio")
    private String username;
    @NotBlank(message = "El email no puede estar vacio")
    @Email(message = "El email ingresado no tiene un formato valido")
    private String email;
    @NotBlank(message = "La contraseña no puede estar vacia")
    private String password;
    private String image;
    private Set<Long> roleIds; // Lista de IDs de roles
}
