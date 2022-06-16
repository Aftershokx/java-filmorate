package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {

    void create (User user);

    void update (User user) throws NotFoundException;

    void delete (int id) throws NotFoundException;

    List<User> getUsers ();

    User getUserWithId (int id) throws NotFoundException;

    List<Integer> getAllUsersId ();

}