package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Override
    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        return genreStorage.findWithId(id);
    }

}