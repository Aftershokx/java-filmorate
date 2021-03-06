package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable int id) throws RuntimeException {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) throws NotFoundException {
        return userService.getWithId(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> allCommonFriends(@PathVariable int id, @PathVariable int otherId) throws RuntimeException {
        return userService.allCommonFriends(id, otherId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) throws ValidationException {
        if (userService.validation(user)) {
            userService.create(user);
        }
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) throws ValidationException {
        if (userService.validation(user)) {
            user = userService.put(user);
        }
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@NotBlank @PathVariable int id, @NotBlank @PathVariable int friendId) throws NotFoundException {
        if (!(id == friendId)
                && userService.getAllUsersID().contains(id)
                && userService.getAllUsersID().contains(friendId)) {
            userService.addFriend(id, friendId);
        } else {
            throw new NotFoundException("???????????? ???????????????????????????? ID");
        }
    }

    @DeleteMapping
    public void remove(@Valid @RequestBody User user) throws NotFoundException {
        userService.delete(user);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) throws RuntimeException {
        if (userService.getWithId(id).getFriends().contains(friendId)) {
            userService.removeFriend(id, friendId);
        } else {
            throw new ValidationException("???????????? ???????????????????????????? ????????");
        }
    }
}
