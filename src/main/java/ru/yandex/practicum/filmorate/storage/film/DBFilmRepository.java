package ru.yandex.practicum.filmorate.storage.film;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@NoArgsConstructor
@Repository
public class DBFilmRepository implements FilmStorage {

    private JdbcTemplate jdbcTemplate;
    private MpaStorage mpaStorage;
    private FilmGenreStorage filmGenreStorage;

    @Autowired
    public DBFilmRepository (JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, FilmGenreStorage filmGenreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.filmGenreStorage = filmGenreStorage;
    }

    @Override
    public Film create (Film film) throws ValidationException {
        if (film.getId () > 0) {
            if (getFilmWithId (film.getId ()) != null) {
                throw new ValidationException ("Фильм с таким Ид уже существует");
            }
        } else {
            String sqlQuery = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)" +
                    " VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder ();
            jdbcTemplate.update (connection -> {
                PreparedStatement stnt = connection.prepareStatement (sqlQuery, new String[]{"FILM_ID"});
                stnt.setString (1, film.getName ());
                stnt.setString (2, film.getDescription ());
                final LocalDate releaseDate = film.getReleaseDate ();
                if (releaseDate == null) {
                    stnt.setNull (3, Types.DATE);
                } else {
                    stnt.setDate (3, Date.valueOf (releaseDate));
                }
                stnt.setLong (4, film.getDuration ());
                if (film.getMpa ().getId () > 0) {
                    stnt.setInt (5, film.getMpa ().getId ());
                } else {
                    stnt.setNull (5, Types.INTEGER);
                }
                return stnt;
            }, keyHolder);
            film.setId (Objects.requireNonNull (keyHolder.getKey ()).intValue ());
            if (film.getLikes () != null) {
                insertLikes (film);
            }
            if (film.getGenres () != null) {
                for (Genre genre : film.getGenres ()) {
                    filmGenreStorage.addNewGenreToFilm (film.getId (), genre);
                }
            }
        }
        return film;
    }

    @Override
    public Film update (Film film) throws NotFoundException {
        if (film.getId () < 1) {
            throw new NotFoundException ("Фильм с ид " + film.getId () + " не обнаружен");
        } else {
            final String sqlQuery = "UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, DURATION = ?, " +
                    "RELEASE_DATE = ?, MPA_ID = ? WHERE FILM_ID = ?";
            jdbcTemplate.update (sqlQuery, film.getName (), film.getDescription (),
                    film.getDuration (), film.getReleaseDate (), film.getMpa ().getId (), film.getId ());
            if (getFilmWithId (film.getId ()).getLikes ().size () > 0 &&
                    getFilmWithId (film.getId ()).getLikes () != null) {
                deleteLikes (getFilmWithId (film.getId ()));
                insertLikes (film);
            }
            if (film.getGenres () != null) {
                filmGenreStorage.updateAllGenresForFilm (film);
                List<Genre> updatedGenres = new ArrayList<> (filmGenreStorage.findAllWithFilmId (film.getId ()));
                film.setGenres (updatedGenres);
            }
        }
        return film;
    }

    @Override
    public void delete (int id) throws NotFoundException {
        if (getFilmWithId (id) == null) {
            throw new NotFoundException ("Фильм с ид " + id + " не существует");
        } else {
            deleteLikes (getFilmWithId (id));
            final String sqlQuery = "DELETE FROM FILMS WHERE FILM_ID = ?";
            jdbcTemplate.update (sqlQuery, id);
        }
    }

    @Override
    public List<Film> getFilms () {
        final String sqlQuery = "SELECT * FROM FILMS";
        List<Film> films = jdbcTemplate.query (sqlQuery, this::makeFilm);
        for (Film film : films) {
            if (film.getMpa ().getId () > 0) {
                if (mpaStorage.getWithId (film.getMpa ().getId ()).isPresent ()) {
                    film.setMpa (mpaStorage.getWithId (film.getMpa ().getId ()).get ());
                }
            }
        }
        return films;
    }

    @Override
    public Film getFilmWithId (int id) throws NotFoundException {
        final String sqlQuery = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        final List<Film> films = jdbcTemplate.query (sqlQuery, this::makeFilm, id);
        if (films.size () != 1) {
            throw new NotFoundException ("Фильм с ид " + id + " не найден");
        }
        if (films.get (0).getMpa ().getId () > 0) {
            if (mpaStorage.getWithId (films.get (0).getMpa ().getId ()).isPresent ()) {
                films.get (0).setMpa (mpaStorage.getWithId (films.get (0).getMpa ().getId ()).get ());
            }
        }
        return films.get (0);
    }

    @Override
    public List<Film> getMostPopular (Integer count) {
        String sql = "SELECT F.FILM_ID,\n" +
                "       F.FILM_NAME,\n" +
                "       F.DESCRIPTION,\n" +
                "       F.RELEASE_DATE,\n" +
                "       F.DURATION,\n" +
                "       F.MPA_ID\n" +
                "FROM FILMS F\n" +
                "         LEFT JOIN LIKES L on F.FILM_ID = L.FILM_ID\n" +
                "GROUP BY F.FILM_ID, L.USER_ID\n" +
                "ORDER BY COUNT(L.USER_ID) DESC\n" +
                "LIMIT ?";
        Set<Film> films = new HashSet<> (jdbcTemplate.query (sql, this::makeFilm, count));
        for (Film film : films) {
            if (film.getMpa ().getId () > 0) {
                if (mpaStorage.getWithId (film.getMpa ().getId ()).isPresent ()) {
                    film.setMpa (mpaStorage.getWithId (film.getMpa ().getId ()).get ());
                }
            }
        }
        return new ArrayList<> (films);
    }

    @Override
    public void insertLikes (Film film) {
        String sql = "INSERT INTO LIKES (USER_ID, FILM_ID) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder ();
        if (getLikesByFilm (film).size () > 0) {
            deleteLikes (film);
        }
        for (Integer like : film.getLikes ()) {
            jdbcTemplate.update (connection -> {
                PreparedStatement stnt = connection.prepareStatement (sql, new String[]{"USER_ID", "FILM_ID"});
                stnt.setInt (1, like);
                stnt.setInt (2, film.getId ());
                return stnt;
            }, keyHolder);
        }
    }

    @Override
    public void deleteLikes (Film film) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ?";
        jdbcTemplate.update (sql, film.getId ());
    }

    @Override
    public void deleteLike (int filmId, int userId) {
        String sql = "DELETE\n" +
                "FROM LIKES\n" +
                "WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update (sql, filmId, userId);
    }

    private Set<Integer> getLikesByFilm (Film film) {
        final String sql = "SELECT * FROM LIKES WHERE FILM_ID = ?";
        List<Integer> likess = jdbcTemplate.query (sql, (rs, rowNum) ->
                rs.getInt ("USER_ID"), film.getId ());
        return new HashSet<> (likess);
    }

    private Film makeFilm (ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film ();
        film.setId (rs.getInt ("FILM_ID"));
        film.setName (rs.getString ("FILM_NAME"));
        film.setDescription (rs.getString ("DESCRIPTION"));
        film.setReleaseDate (rs.getDate ("RELEASE_DATE").toLocalDate ());
        film.setDuration (rs.getLong ("DURATION"));
        film.setMpa (new Mpa (rs.getInt ("MPA_ID")));
        List<Genre> genres = new ArrayList<> ();
        if (filmGenreStorage.findAllWithFilmId (film.getId ()) != null) {
            genres.addAll (filmGenreStorage.findAllWithFilmId (film.getId ()));
        }
        film.setGenres (genres);
        film.setLikes (getLikesByFilm (film));
        return film;
    }
}
