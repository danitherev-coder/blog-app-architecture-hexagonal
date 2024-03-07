package com.dani.roles.domain.model;

import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private Long id;
    private String name;

    public Role(String name) {
        this.name = name;
    }
}
