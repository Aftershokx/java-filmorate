package ru.yandex.practicum.filmorate.model;

import java.util.*;
import java.util.stream.Collectors;

public class Friendship {
    public static List<Integer> findCrossings (List<Integer> userId, List<Integer> friendsId) {
        return userId.stream ()
                .distinct ()
                .filter (friendsId::contains)
                .collect (Collectors.toList ());
    }
}