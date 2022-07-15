package ru.yandex.practicum.filmorate.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Genre {
    private int id;
    private String name;
}