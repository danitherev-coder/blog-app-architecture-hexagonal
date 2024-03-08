package com.dani.roles.domain.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Login {
    private String username;
    private String password;
    private String token;
}
