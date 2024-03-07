package com.dani.roles.infrastructure.adapters.input.rest.model.response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String image;
}
