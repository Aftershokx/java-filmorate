package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserServiceInterface {
    User create (User user) throws ValidationException;

    User put (User user) throws ValidationException;

    void delete (User user) throws NotFoundException;

    Collection<User> findAll ();

    User getWithId (int id) throws NotFoundException;

    boolean validation (User user);

    List<Integer> getAllUsersID ();

    void addFriend (int id, int friendId) throws NotFoundException;

    void removeFriend (int id, int friendId) throws ValidationException, NotFoundException;

    List<User> getAllFriends (int id) throws NotFoundException;

    List<User> allCommonFriends (int id, int otherId) throws NotFoundException;

}
