package ru.yandex.practicum.filmorate.service.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Optional;

@Service
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    public MpaServiceImpl (MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Optional<Mpa> getWithId (int id) {
        return mpaStorage.getWithId (id);
    }

    @Override
    public List<Mpa> getAll () {
        return mpaStorage.getAll ();
    }

}