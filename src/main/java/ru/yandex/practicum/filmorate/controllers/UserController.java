package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController (UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll () {
        return userService.findAll ();
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends (@PathVariable int id) throws RuntimeException {
        return userService.getAllFriends (id);
    }

    @GetMapping("/{id}")
    public User getUserById (@PathVariable int id) throws NotFoundException {
        return userService.getUserById (id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> allCommonFriends (@PathVariable int id, @PathVariable int otherId) throws RuntimeException {
        return userService.allCommonFriends (id, otherId);
    }

    @PostMapping
    public User create (@Valid @RequestBody User user) throws ValidationException {
        return userService.create (user);
    }

    @PutMapping
    public User put (@Valid @RequestBody User user) throws ValidationException {
        return userService.put (user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend (@NotBlank @PathVariable int id, @NotBlank @PathVariable int friendId) throws NotFoundException {
        userService.addFriend (id, friendId);
    }

    @DeleteMapping
    public void remove (@Valid @RequestBody User user) throws NotFoundException {
        userService.removeUser (user);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend (@PathVariable int id, @PathVariable int friendId) throws RuntimeException {
        userService.removeFriend (id, friendId);
    }
}
