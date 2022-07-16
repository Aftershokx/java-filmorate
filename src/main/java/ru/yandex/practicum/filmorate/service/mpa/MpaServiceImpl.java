package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Override
    public Optional<Mpa> getWithId(int id) {
        return mpaStorage.getWithId(id);
    }

    @Override
    public List<Mpa> getAll() {
        return mpaStorage.getAll();
    }

}