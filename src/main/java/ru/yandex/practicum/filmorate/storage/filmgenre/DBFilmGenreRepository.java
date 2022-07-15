package ru.yandex.practicum.filmorate.storage.filmgenre;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@NoArgsConstructor
@Repository
public class DBFilmGenreRepository implements FilmGenreStorage {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DBFilmGenreRepository (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Set<Genre> findAllWithFilmId (int id) {
        Set<Genre> genres = new HashSet<> ();
        String sql = "SELECT * FROM FILM_GENRES G JOIN GENRES G2 on G2.GENRE_ID = G.GENRE_ID " +
                "WHERE FILM_ID = ? ORDER BY 1";
        SqlRowSet rs = jdbcTemplate.queryForRowSet (sql, id);
        while (rs.next ()) {
            genres.add (new Genre (rs.getInt ("GENRE_ID"),
                    rs.getString ("GENRE_NAME")));
        }

        return genres;
    }

    @Override
    public void addNewGenreToFilm (int filmId, Genre genre) {
        String sql = "INSERT INTO FILM_GENRES(FILM_ID, GENRE_ID) values  (?, ?)";
        jdbcTemplate.update (sql, filmId, genre.getId ());
    }

    @Override
    public void updateAllGenresForFilm (Film film) {
        if (film.getGenres () != null) {
            if (film.getGenres ().isEmpty ()) {
                deleteAll (film);
                return;
            }
            deleteAll (film);
            StringBuilder sb = new StringBuilder ("INSERT INTO FILM_GENRES (GENRE_ID, FILM_ID) VALUES ");
            Set<Genre> genres = new HashSet<> (film.getGenres ());
            for (Genre genre : genres) {
                sb.append ("(").append (genre.getId ()).append (",").append (film.getId ()).append ("),");
            }
            String sql = sb.subSequence (0, sb.length () - 1).toString ();
            jdbcTemplate.update (sql);
        }
    }

    private void deleteAll (Film film) {
        final String sql = "DELETE FROM FILM_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update (sql, film.getId ());
    }
}
