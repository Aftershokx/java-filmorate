package ru.yandex.practicum.filmorate.storage.film;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utility.IdUpdater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<> ();
    private final IdUpdater idUpdater = new IdUpdater ();

    @Override
    public void create (Film film) {
        if (films.containsKey (film.getId ())) {
            throw new ValidationException ("Произошла ошибка при создании фильма, фильм с таким ИД уже существует");
        } else {
            film.setId (idUpdater.updateId (film.getId (), films.keySet ()));
            films.put (film.getId (), film);
            log.info ("Фильм " + film + " Успешно добавлен");
        }
    }

    @Override
    public void update (Film film) throws NotFoundException {
        if (!films.containsKey (film.getId ())) {
            throw new NotFoundException
                    ("Произошла ошибка при обновлении фильма, фильм с таким ИД не существует");
        } else {
            films.put (film.getId (), film);
            log.info ("Фильм " + film + " Успешно обновлен");
        }
    }

    @Override
    public void delete (int id) throws NotFoundException {
        if (!films.containsKey (id)) {
            throw new NotFoundException ("Произошла ошибка при удалении фильма с ИД " + id +
                    ", фильм с таким ИД не существует ");
        } else {
            films.remove (id);
            log.info ("Фильм с ИД " + id + " успешно удален");
        }
    }

    @Override
    public List<Film> getFilms () {
        return new ArrayList<> (films.values ());
    }

    @Override
    public Film getFilmWithId (int id) throws NotFoundException {
        if (!films.containsKey (id)) {
            throw new NotFoundException ("Произошла ошибка при поиске фильма, фильм с таким ИД не существует");
        }
        return films.get (id);
    }


}
