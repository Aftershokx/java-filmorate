package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Getter
@Slf4j
public class UserService {
    private final FilmService filmService;
    private final UserStorage userStorage;

    @Autowired
    public UserService (FilmService filmService, UserStorage userStorage) {
        this.filmService = filmService;
        this.userStorage = userStorage;
    }

    public Collection<User> findAll () {
        return userStorage.getUsers ();
    }

    public User create (User user) throws ValidationException {
        if (validationForUser (user)) {
            userStorage.create (user);
        }
        return user;
    }

    public User put (User user) throws ValidationException {
        if (validationForUser (user)) {
            userStorage.update (user);
        }
        return user;
    }

    public void removeUser (User user) throws NotFoundException {
        userStorage.delete (user.getId ());
    }

    public User getUserById (int id) throws NotFoundException {
        return userStorage.getUserWithId (id);
    }

    public List<Integer> getAllUsersID () {
        return userStorage.getAllUsersId ();
    }

    public void addFriend (int id, int friendId) throws NotFoundException {
        if (!(id == friendId)
                && userStorage.getAllUsersId ().contains (id)
                && userStorage.getAllUsersId ().contains (friendId)) {
            userStorage.getUserWithId (id).getFriends ().add (friendId);
            userStorage.getUserWithId (friendId).getFriends ().add (id);
        } else {
            throw new NotFoundException ("Указан несуществующий ID");
        }
    }

    public void removeFriend (int id, int friendId) throws ValidationException, NotFoundException {
        if (userStorage.getUserWithId (id).getFriends ().contains (friendId)
                && userStorage.getUserWithId (friendId).getFriends ().contains (id)) {
            userStorage.getUserWithId (id).getFriends ().remove (friendId);
            userStorage.getUserWithId (friendId).getFriends ().remove (id);
        } else {
            throw new ValidationException ("Указан несуществующий друг");
        }
    }

    public List<User> getAllFriends (int id) throws NotFoundException {
        List<User> friendsList = new ArrayList<> ();
        for (Integer friend : userStorage.getUserWithId (id).getFriends ()) {
            friendsList.add (userStorage.getUserWithId (friend));
        }
        return friendsList;
    }

    public List<User> allCommonFriends (int id, int otherId) throws NotFoundException {
        List<User> commonFriends = new ArrayList<> ();
        for (Integer friend : userStorage.getUserWithId (id).getFriends ()) {
            if (userStorage.getUserWithId (otherId).getFriends ().contains (friend)) {
                commonFriends.add (userStorage.getUserWithId (friend));
            }
        }
        return commonFriends;
    }

    private boolean validationForUser (User user) {
        if (user.getEmail ().isBlank () || user.getEmail ().isEmpty () || !user.getEmail ().contains ("@")) {
            throw new ValidationException ("Электронная почта не может быть пустой и должна содержать символ @");
        } else if (user.getLogin ().isBlank () || user.getLogin ().isEmpty () || user.getLogin ().contains (" ")) {
            throw new ValidationException ("Логин не может быть пустым и содержать пробелы");
        } else if (user.getBirthday ().isAfter (LocalDate.now ())) {
            throw new ValidationException ("Дата рождения не может быть в будущем");
        } else if (user.getName ().isEmpty () || user.getName ().isBlank ()) {
            user.setName (user.getLogin ());
        }
        return true;
    }
}
