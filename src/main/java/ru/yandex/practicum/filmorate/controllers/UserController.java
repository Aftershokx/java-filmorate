package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.IdUpdater;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<> ();
    private final IdUpdater idUpdater = new IdUpdater ();

    @GetMapping
    public Collection<User> findAll () {
        return users.values ();
    }

    @PostMapping
    public User create (@Valid @RequestBody User user) throws ValidationException {
        if (user == null || users.containsKey (user.getId ())) {
            throw new ValidationException ("Произошла ошибка при создании пользователя, введите корректные данные");
        }
        if (user.getName () == null || user.getName ().isBlank ()) {
            user.setName (user.getLogin ());
        }
        user.setId (idUpdater.updateId (user.getId (), users.keySet ()));
        users.put (user.getId (), user);
        log.info ("Пользователь " + user + " Успешно добавлен");
        return user;
    }

    @PutMapping
    public User put (@Valid @RequestBody User user) throws ValidationException {
        if (user == null || !users.containsKey (user.getId ())) {
            throw new ValidationException ("Произошла ошибка при обновлении пользователя, введите корректные данные");
        }
        if (user.getName ().isBlank ()) {
            user.setName (user.getLogin ());
        }
        users.put (user.getId (), user);
        log.info ("Пользователь " + user + " Успешно обновлен");
        return user;
    }

}
