package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public Collection<User> findAll() {
        return userStorage.getUsers();
    }

    @Override
    public User create(User user) throws ValidationException {
        userStorage.create(user);
        return user;
    }

    @Override
    public User put(User user) throws ValidationException {
        userStorage.update(user);
        return user;
    }

    @Override
    public void delete(User user) throws NotFoundException {
        userStorage.delete(user.getId());
    }

    @Override
    public User getWithId(int id) throws NotFoundException {
        return userStorage.getUserWithId(id);
    }

    @Override
    public boolean validation(User user) {
        if (user.getEmail().isBlank() || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        } else if (user.getLogin().isBlank() || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        } else if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return true;
    }

    @Override
    public List<Integer> getAllUsersID() {
        return userStorage.getAllUsersId();
    }

    @Override
    public void addFriend(int id, int friendId) throws NotFoundException {
        User user = userStorage.getUserWithId(id);
        user.getFriends().add(friendId);
        userStorage.update(user);
    }

    @Override
    public void removeFriend(int id, int friendId) throws ValidationException, NotFoundException {
        User user = userStorage.getUserWithId(id);
        user.getFriends().remove(friendId);
        userStorage.update(user);
    }

    @Override
    public List<User> getAllFriends(int id) throws NotFoundException {
        return userStorage.getUserFriends(id);
    }

    @Override
    public List<User> allCommonFriends(int id, int otherId) throws NotFoundException {
        return userStorage.getUserCrossFriends(id, otherId);
    }


}
