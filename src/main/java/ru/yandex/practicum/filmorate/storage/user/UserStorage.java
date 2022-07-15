package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStorage {

    User create (User user) throws ValidationException;

    Optional<User> update (User user) throws NotFoundException;

    void delete (int id) throws NotFoundException;

    List<User> getUsers ();

    User getUserWithId (int id) throws NotFoundException;

    List<Integer> getAllUsersId ();

    List<User> getUserCrossFriends (int id, int otherId);

    List<User> getUserFriends (int id);


}