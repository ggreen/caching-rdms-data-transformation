package com.github.ggreen.caching.rdms.domain;

import lombok.*;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account
{
    private Long id;
    private String name;
}
