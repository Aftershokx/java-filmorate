package ru.yandex.practicum.filmorate.storage.user;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@NoArgsConstructor
@Repository
public class DBUserRepository implements UserStorage {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DBUserRepository (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create (User user) throws ValidationException {
        if (user.getId () != 0 && getUserWithId (user.getId ()) != null) {
            throw new ValidationException ("Пользователь с таким Ид уже существует");
        } else {
            String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder ();
            jdbcTemplate.update (connection -> {
                PreparedStatement stnt = connection.prepareStatement (sqlQuery, new String[]{"USER_ID"});
                stnt.setString (1, user.getEmail ());
                stnt.setString (2, user.getLogin ());
                stnt.setString (3, user.getName ());
                final LocalDate birthday = user.getBirthday ();
                if (birthday == null) {
                    stnt.setNull (4, Types.DATE);
                } else {
                    stnt.setDate (4, Date.valueOf (birthday));
                }
                return stnt;
            }, keyHolder);
            user.setId (Objects.requireNonNull (keyHolder.getKey ()).intValue ());
        }
        return user;
    }

    @Override
    public Optional<User> update (User user) throws NotFoundException {
        if (getUserWithId (user.getId ()) == null) {
            throw new NotFoundException ("Пользователь с ид " + " не обнаружен");
        } else {
            final String sqlQuery = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, USER_NAME = ?, " +
                    "BIRTHDAY = ? WHERE USER_ID = ?";
            jdbcTemplate.update (sqlQuery, user.getEmail (), user.getLogin (), user.getName ()
                    , user.getBirthday ()
                    , user.getId ());
            deleteFriends (user);
            insertFriends (user);
        }
        return Optional.of (user);
    }

    @Override
    public void delete (int id) throws NotFoundException {
        if (getUserWithId (id) == null) {
            throw new NotFoundException ("Пользователя с ид " + id + " не существует");
        } else {
            deleteFriends (getUserWithId (id));
            final String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
            jdbcTemplate.update (sqlQuery, id);
        }
    }

    @Override
    public List<User> getUsers () {
        final String sqlQuery = "SELECT * FROM USERS";
        return jdbcTemplate.query (sqlQuery, DBUserRepository::makeUser);
    }

    @Override
    public User getUserWithId (int id) throws NotFoundException {
        final String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        final List<User> users = jdbcTemplate.query (sqlQuery, DBUserRepository::makeUser, id);
        if (users.size () != 1) {
            throw new NotFoundException ("Пользователь с ид " + id + " не найден");
        }
        List<User> friends = getUserFriends (users.get (0).getId ());
        for (User friend : friends) {
            users.get (0).getFriends ().add (friend.getId ());
        }
        return users.get (0);
    }

    @Override
    public List<Integer> getAllUsersId () {
        final String sql = "SELECT * FROM USERS";
        List<Integer> usersIds = new ArrayList<> ();
        SqlRowSet rs = jdbcTemplate.queryForRowSet (sql);
        while (rs.next ()) {
            usersIds.add (rs.getInt ("USER_ID"));
        }
        return usersIds;
    }

    @Override
    public List<User> getUserFriends (int id) {
        final String sql = "SELECT * FROM USERS WHERE USER_ID IN (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?)";
        return jdbcTemplate.query (sql, DBUserRepository::makeUser, id);
    }


    @Override
    public List<User> getUserCrossFriends (int id, int otherId) {
        final String sql = "SELECT * FROM USERS WHERE USER_ID IN (SELECT FRIEND_ID " +
                "FROM FRIENDS WHERE USER_ID = " + id + ") " +
                "AND USER_ID IN (SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = " + otherId + ")";

        return jdbcTemplate.query (sql, DBUserRepository::makeUser);
    }

    private void deleteFriends (User user) {
        final String sql = "DELETE FROM FRIENDS where USER_ID = ?";
        jdbcTemplate.update (sql, user.getId ());
    }


    private void insertFriends (User user) {
        if (user.getFriends ().isEmpty ()) {
            return;
        }
        String sql = "INSERT INTO FRIENDS (FRIEND_ID, USER_ID) values (?, ?)";

        try (PreparedStatement ps = Objects.requireNonNull (jdbcTemplate.getDataSource ()).
                getConnection ().
                prepareStatement (sql)) {
            for (Integer friend : user.getFriends ()) {
                ps.setInt (1, friend);
                ps.setInt (2, user.getId ());
                ps.addBatch ();
                ps.executeUpdate ();
            }
        } catch (SQLException e) {
            e.printStackTrace ();
        }
    }

    static User makeUser (ResultSet rs, int rowNum) throws SQLException {
        User user = new User ();
        user.setId (rs.getInt ("USER_ID"));
        user.setEmail (rs.getString ("EMAIL"));
        user.setLogin (rs.getString ("LOGIN"));
        user.setName (rs.getString ("USER_NAME"));
        user.setBirthday (rs.getDate ("BIRTHDAY").toLocalDate ());
        return user;
    }
}
