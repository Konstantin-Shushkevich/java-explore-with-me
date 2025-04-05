package ru.practicum.dto;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class StatsDto {

    private String app;

    private String uri;

    private Long hits;
}
