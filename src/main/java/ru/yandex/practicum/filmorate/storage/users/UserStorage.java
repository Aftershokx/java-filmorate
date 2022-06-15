package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {

    void createUser (User user);

    void updateUser (User user) throws NotFoundException;

    void deleteUser (int id) throws NotFoundException;

    List<User> getUsers ();

    User getUserWithId (int id) throws NotFoundException;

    List<Integer> getAllUsersId ();

}