package ru.yandex.practicum.filmorate.storage.users;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.IdUpdater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<> ();
    private final IdUpdater idUpdater = new IdUpdater ();

    @Override
    public void create (User user) {
        if (users.containsKey (user.getId ())) {
            throw new ValidationException
                    ("Произошла ошибка при создании пользователя, пользователь с таким ИД уже существует");
        } else {
            user.setId (idUpdater.updateId (user.getId (), users.keySet ()));
            users.put (user.getId (), user);
            log.info ("Пользователь " + user + " Успешно добавлен");
        }
    }

    @Override
    public void delete (int id) throws NotFoundException {
        if (!users.containsKey (id)) {
            throw new NotFoundException ("Произошла ошибка при удалении пользователя, пользователь с таким ИД не существует");
        } else {
            users.remove (id);
            log.info ("Пользователь с ИД " + id + " успешно удален");
        }
    }

    @Override
    public User getUserWithId (int id) throws NotFoundException {
        if (!users.containsKey (id)) {
            throw new NotFoundException ("Произошла ошибка при поиске пользователя, пользователь с таким ИД не существует");
        }
        return users.get (id);
    }

    @Override
    public List<Integer> getAllUsersId () {
        return new ArrayList<> (users.keySet ());
    }

    @Override
    public List<User> getUsers () {
        return new ArrayList<> (users.values ());
    }

    @Override
    public void update (User user) throws NotFoundException {
        if (!users.containsKey (user.getId ())) {
            throw new NotFoundException ("Произошла ошибка при обновлении пользователя, пользователь с таким ИД не существует");
        } else {
            users.put (user.getId (), user);
            log.info ("Пользователь " + user + " Успешно обновлен");
        }
    }

}
