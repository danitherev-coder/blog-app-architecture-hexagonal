package com.dani.roles.domain.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String description;
    private String content;
    private String cover;
    private Long catIds;
    private User user;
}
