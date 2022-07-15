package ru.yandex.practicum.filmorate.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Mpa {
    int id;
    String name;

    public Mpa (int id) {
        this.id = id;
    }
}