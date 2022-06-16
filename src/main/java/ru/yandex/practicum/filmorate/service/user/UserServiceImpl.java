package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl (UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<User> findAll () {
        return userStorage.getUsers ();
    }

    @Override
    public User create (User user) throws ValidationException {
        userStorage.create (user);
        return user;
    }

    @Override
    public User put (User user) throws ValidationException {
        userStorage.update (user);
        return user;
    }

    @Override
    public void delete (User user) throws NotFoundException {
        userStorage.delete (user.getId ());
    }

    @Override
    public User getWithId (int id) throws NotFoundException {
        return userStorage.getUserWithId (id);
    }

    @Override
    public boolean validation (User user) {
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

    @Override
    public List<Integer> getAllUsersID () {
        return userStorage.getAllUsersId ();
    }

    @Override
    public void addFriend (int id, int friendId) throws NotFoundException {
        userStorage.getUserWithId (id).getFriends ().add (friendId);
        userStorage.getUserWithId (friendId).getFriends ().add (id);
    }

    @Override
    public void removeFriend (int id, int friendId) throws ValidationException, NotFoundException {
        userStorage.getUserWithId (id).getFriends ().remove (friendId);
        userStorage.getUserWithId (friendId).getFriends ().remove (id);
    }

    @Override
    public List<User> getAllFriends (int id) throws NotFoundException {
        List<User> friendsList = new ArrayList<> ();
        for (Integer friend : userStorage.getUserWithId (id).getFriends ()) {
            friendsList.add (userStorage.getUserWithId (friend));
        }
        return friendsList;
    }

    @Override
    public List<User> allCommonFriends (int id, int otherId) throws NotFoundException {
        List<User> commonFriends = new ArrayList<> ();
        for (Integer friend : userStorage.getUserWithId (id).getFriends ()) {
            if (userStorage.getUserWithId (otherId).getFriends ().contains (friend)) {
                commonFriends.add (userStorage.getUserWithId (friend));
            }
        }
        return commonFriends;
    }


}
