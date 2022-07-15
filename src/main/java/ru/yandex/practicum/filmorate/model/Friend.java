package ru.yandex.practicum.filmorate.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Friend {
    private long userId;
    private long friendId;
    private boolean isCross;
}