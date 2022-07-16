package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class DBMpaRepository implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Mpa> getWithId(int id) {
        final String sql = "SELECT * FROM MPA where MPA_ID = ?";
        return jdbcTemplate.queryForStream(sql, (rs, rowNum) ->
                new Mpa(rs.getInt(1), rs.getString(2)), id).findFirst();
    }

    @Override
    public List<Mpa> getAll() {
        final String sql = "SELECT * FROM MPA ORDER BY 1";

        return jdbcTemplate.queryForStream(sql, (rs, rowNum) ->
                        new Mpa(rs.getInt(1), rs.getString(2)))
                .sorted((o1, o2) -> o1.getId() < o2.getId() ? -1 : 1)
                .collect(Collectors.toList());
    }

}
