package ru.yandex.practicum.filmorate.utility;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class IdUpdater {

    private int uniId;

    public int updateId (int id, Set<Integer> existedIds) {
        if (id > 0) {
            uniId = id;
        } else {
            int maxId = 0;
            for (Integer currentId : existedIds) {
                if (currentId > maxId) {
                    maxId = currentId;
                }
            }
            uniId = maxId + 1;
        }

        return uniId;
    }
}
