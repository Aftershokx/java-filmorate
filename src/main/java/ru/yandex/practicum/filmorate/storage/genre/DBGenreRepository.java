package ru.yandex.practicum.filmorate.storage.genre;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
@Repository
public class DBGenreRepository implements GenreStorage {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DBGenreRepository (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll () {
        final String query = "SELECT * FROM GENRES";
        return jdbcTemplate.queryForStream (query, (rs, rowNum) -> new Genre (rs.getInt ("GENRE_ID"),
                rs.getString ("GENRE_NAME"))).collect (Collectors.toList ());
    }

    @Override
    public Optional<Genre> findWithId (Integer id) {
        final String query = "SELECT * FROM GENRES WHERE GENRE_ID =  ?";
        SqlRowSet rs = jdbcTemplate.queryForRowSet (query, id);
        if (rs.next ()) {
            return Optional.of (new Genre (rs.getInt (1), rs.getString (2)));
        }
        return Optional.empty ();
    }

}
