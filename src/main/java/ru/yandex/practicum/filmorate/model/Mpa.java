package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mpa {
    int id;
    String name;

    public Mpa (int id) {
        this.id = id;
    }
}