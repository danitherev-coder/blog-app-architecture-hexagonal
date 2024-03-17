package com.dani.roles.domain.model;

import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;
    private String image;
    private Set<Long> roleIds;
}
