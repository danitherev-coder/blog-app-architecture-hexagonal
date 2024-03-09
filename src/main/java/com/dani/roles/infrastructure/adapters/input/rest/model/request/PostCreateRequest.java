package com.dani.roles.infrastructure.adapters.input.rest.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {
    @NotBlank(message = "El titulo no puede estar vacio")
    private String title;
    @NotBlank(message = "La descripcion no puede estar vacia")
    private String description;
    @NotBlank(message = "El contenido no puede estar vacio")
    private String content;
    @NotBlank(message = "La portada no puede estar vacia")
    private String cover;
    @NotNull(message = "La categoria no puede estar vacia")
    private Long catIds;
}
