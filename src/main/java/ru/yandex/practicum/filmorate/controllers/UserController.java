package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utility.IdUpdater;

import javax.validation.Valid;
import java.time.LocalDate;
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
        validationForUser (user);
        if (users.containsKey (user.getId ())) {
            throw new ValidationException
                    ("Произошла ошибка при создании пользователя, пользователь с таким ИД уже существует");
        }
        user.setId (idUpdater.updateId (user.getId (), users.keySet ()));
        users.put (user.getId (), user);
        log.info ("Пользователь " + user + " Успешно добавлен");
        return user;
    }

    @PutMapping
    public User put (@Valid @RequestBody User user) throws ValidationException {
        validationForUser (user);
        if (!users.containsKey (user.getId ())) {
            throw new ValidationException
                    ("Произошла ошибка при обновлении пользователя, пользователь с таким ИД не существует");
        }
        users.put (user.getId (), user);
        log.info ("Пользователь " + user + " Успешно обновлен");
        return user;
    }

    private void validationForUser (User user) {
        if (user.getEmail ().isBlank () || user.getEmail ().isEmpty () || !user.getEmail ().contains ("@")) {
            throw new ValidationException ("Электронная почта не может быть пустой и должна содержать символ @");
        } else if (user.getLogin ().isBlank () || user.getLogin ().isEmpty () || user.getLogin ().contains (" ")) {
            throw new ValidationException ("Логин не может быть пустым и содержать пробелы");
        } else if (user.getBirthday ().isAfter (LocalDate.now ())) {
            throw new ValidationException ("Дата рождения не может быть в будущем");
        } else if (user.getName ().isEmpty () || user.getName ().isBlank ()) {
            user.setName (user.getLogin ());
        }
    }

}
