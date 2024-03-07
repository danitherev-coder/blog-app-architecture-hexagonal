package com.dani.roles.infrastructure.adapters.input.rest.model.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateRequest {
    @NotBlank(message = "El nombre de la categoria no puede estar vacio")
    private String name;
}
