package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final MockMvc mvc;
    private final ObjectMapper mapper;

    private User user;
    private Film film;

    @BeforeEach
    public void reloadModels () {
        user = new User ();
        user.setEmail ("user@gmail.com");
        user.setName ("after");
        user.setLogin ("aft");
        user.setBirthday (LocalDate.of (1980, 1, 1));
        film = new Film ();
        film.setName ("Film");
        film.setDescription ("Film description about film");
        film.setReleaseDate (LocalDate.now ().minusDays (1));
        film.setDuration (2600);
        film.setMpa (new Mpa (1, "G"));
        List<Genre> genres = new ArrayList<> ();
        film.setGenres (genres);
    }

    @DisplayName("Проверка на создание валидного пользователя")
    @DirtiesContext
    @Test
    public void createCorrectUserTest () throws Exception {
        User user1 = new User ();
        user1.setEmail ("user@gmail.com");
        user1.setName ("after");
        user1.setLogin ("aft");
        user1.setBirthday (LocalDate.of (1980, 1, 1));
        postWithOkRequest (user1, "/users");
        List<User> users = userStorage.getUsers ();
        System.out.println (users);
        Assertions.assertEquals (1, userStorage.getUsers ().size ());
    }

    @DisplayName("Проверка на создание пользователя с null email")
    @Test
    public void createEmptyEmailUserTest () throws Exception {
        user.setEmail (null);
        postWithBadRequest (user, "/users");
        Assertions.assertNotEquals (1, userStorage.getAllUsersId ().size ());
    }

    @DisplayName("Проверка на создание пользователя с пустым email")
    @Test
    public void createBlankEmailUserTest () throws Exception {
        user.setEmail (" ");
        postWithBadRequest (user, "/users");
        Assertions.assertNotEquals (1, userStorage.getAllUsersId ().size ());
    }

    @DisplayName("Проверка на создание пользователя с некорректным email")
    @Test
    public void createNonEmailUserTest () throws Exception {
        user.setEmail ("userGmail.ru");
        postWithBadRequest (user, "/users");
        Assertions.assertNotEquals (1, userStorage.getAllUsersId ().size ());
    }

    @DisplayName("Проверка на создание пользователя с пустым логином состоящим из пробелов")
    @Test
    public void createBlankLoginUserTest () throws Exception {
        user.setLogin (" ");
        postWithBadRequest (user, "/users");
        Assertions.assertNotEquals (1, userStorage.getUsers ().size ());
    }

    @DisplayName("Проверка на создание пользователя с пробелами в логине")
    @Test
    public void createLoginWithSpacesUserTest () throws Exception {
        user.setLogin ("user login");
        postWithBadRequest (user, "/users");
        Assertions.assertNotEquals (1, userStorage.getAllUsersId ().size ());
    }

    @DisplayName("Проверка на создание пользователя с днем рождения в будущем")
    @Test
    public void createFutureBirthdayUserTest () throws Exception {
        user.setBirthday (LocalDate.now ().plusDays (1));
        postWithBadRequest (user, "/users");
        Assertions.assertNotEquals (1, userStorage.getAllUsersId ().size ());
    }

    @DisplayName("Проверка на создание корректного фильма")
    @DirtiesContext
    @Test
    public void createCorrectFilmTest () throws Exception {
        film.getGenres ().add (new Genre (1, "Комедия"));
        postWithOkRequest (film, "/films");
        Assertions.assertEquals (1, filmStorage.getFilms ().size ());
    }

    @DisplayName("Проверка на создание фильма без названия")
    @Test
    public void createEmptyNameFilmTest () throws Exception {
        film.setName (null);
        postWithBadRequest (film, "/films");
        Assertions.assertNotEquals (1, filmStorage.getFilms ().size ());
    }

    @DisplayName("Проверка на создание фильма с пустым названием")
    @Test
    public void createBlankNameFilmTest () throws Exception {
        film.setName (" ");
        postWithBadRequest (film, "/films");
        Assertions.assertNotEquals (1, filmStorage.getFilms ().size ());
    }

    @DisplayName("Проверка на создание фильма с описанием длиннее 200 символов")
    @Test
    public void createOutOfSizeDescriptionFilmTest () throws Exception {
        film.setDescription ("e".repeat (204));
        postWithBadRequest (film, "/films");
        Assertions.assertNotEquals (1, filmStorage.getFilms ().size ());
    }

    @DisplayName("Проверка на создание фильма до даты выхода первого фильма")
    @Test
    public void createOutOfReleaseDateFilmTest () throws Exception {
        film.setReleaseDate (LocalDate.of (1895, 12, 27));
        postWithBadRequest (film, "/films");
        Assertions.assertNotEquals (1, filmStorage.getFilms ().size ());
    }

    @DisplayName("Проверка на создание фильма с отрицательной продолжительностью")
    @Test
    public void createNegativeDurationFilmTest () throws Exception {
        film.setDuration (-100);
        postWithBadRequest (film, "/films");
        Assertions.assertNotEquals (1, filmStorage.getFilms ().size ());
    }

    @DisplayName("Проверка на получение списка пользователей")
    @DirtiesContext
    @Test
    public void getUsersTest () throws Exception {
        postWithOkRequest (user, "/users");
        user.setId (1);
        Assertions.assertTrue (userStorage.getUsers ().contains (user));
        JSONArray usersArray = new JSONArray ();
        usersArray.put (new JSONObject (mapper.writeValueAsString (user)));
        mvc.perform (get ("/users"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (usersArray)))
                .andReturn ();

    }

    @DisplayName("Проверка на получение списка фильмов")
    @DirtiesContext
    @Test
    public void getFilmsTest () throws Exception {
        postWithOkRequest (film, "/films");
        film.setId (1);
        Assertions.assertTrue (filmStorage.getFilms ().contains (film));
        JSONArray filmsArray = new JSONArray ();
        filmsArray.put (new JSONObject (mapper.writeValueAsString (film)));
        mvc.perform (get ("/films"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (filmsArray)))
                .andReturn ();

    }

    @DisplayName("Проверка на корректное редактирование фильма с существующим ИД")
    @DirtiesContext
    @Test
    public void putCorrectFilmTest () throws Exception {
        postWithOkRequest (film, "/films");
        film.setDescription ("updatedDesc");
        film.setId (1);
        film.getGenres ().add (new Genre (1, "Комедия"));
        putWithOkRequest (film, "/films");
        Assertions.assertTrue (filmStorage.getFilms ().contains (film));
        JSONArray filmsArray = new JSONArray ();
        filmsArray.put (new JSONObject (mapper.writeValueAsString (film)));
        mvc.perform (get ("/films"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (filmsArray)))
                .andReturn ();
    }

    @DisplayName("Проверка на корректное редактирование фильма с несуществующим ИД")
    @DirtiesContext
    @Test
    public void putFilmWithIncorrectIdTest () throws Exception {
        postWithOkRequest (film, "/films");
        film.setDescription ("updatedDesc");
        film.setId (10);
        putWithBadRequest (film, "/films");
        Assertions.assertFalse (filmStorage.getFilms ().contains (film));
    }

    @DisplayName("Проверка на корректное редактирование пользователя с существующим ИД")
    @DirtiesContext
    @Test
    public void putCorrectUserTest () throws Exception {
        postWithOkRequest (user, "/users");
        user.setName ("updatedName");
        user.setId (1);
        putWithOkRequest (user, "/users");
        Assertions.assertTrue (userStorage.getUsers ().contains (user));
        JSONArray usersArray = new JSONArray ();
        usersArray.put (new JSONObject (mapper.writeValueAsString (user)));
        mvc.perform (get ("/users"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (usersArray)))
                .andReturn ();
    }

    @DisplayName("Проверка на корректное редактирование пользователя с несуществующим ИД")
    @Test
    public void putUserWithIncorrectIdTest () throws Exception {
        postWithOkRequest (user, "/users");
        user.setName ("updatedName");
        user.setId (10);
        putWithBadRequest (user, "/users");
        Assertions.assertFalse (userStorage.getUsers ().contains (user));
    }

    @DisplayName("Проверка на корректное получение фильма по ИД")
    @DirtiesContext
    @Test
    public void getFilmWithIdTest () throws Exception {
        postWithOkRequest (film, "/films");
        film.setId (1);
        Assertions.assertEquals (film, filmStorage.getFilmWithId (1));
        JSONObject jsonObject = new JSONObject (mapper.writeValueAsString (film));
        mvc.perform (get ("/films/1"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (jsonObject)))
                .andReturn ();
    }

    @DisplayName("Проверка на корректное получение пользователя по ИД")
    @DirtiesContext
    @Test
    public void getUserWithIdTest () throws Exception {
        postWithOkRequest (user, "/users");
        user.setId (1);
        Assertions.assertEquals (user, userStorage.getUserWithId (1));
        JSONObject jsonObject = new JSONObject (mapper.writeValueAsString (user));
        mvc.perform (get ("/users/1"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (jsonObject)))
                .andReturn ();
    }

    @DisplayName("Проверка на корректное получение жанра по ИД")
    @Test
    public void getGenreWithIdTest () throws Exception {
        Genre genre = new Genre ();
        genre.setId (1);
        genre.setName ("Комедия");
        if (genreStorage.findWithId (1).isPresent ()) {
            Assertions.assertEquals (genre, genreStorage.findWithId (1).get ());
        }
        JSONObject jsonObject = new JSONObject (mapper.writeValueAsString (genre));
        mvc.perform (get ("/genres/1"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (jsonObject)))
                .andReturn ();
    }

    @DisplayName("Проверка на корректное получение рейтинга по ИД")
    @Test
    public void getMpaWithIdTest () throws Exception {
        Mpa mpa = new Mpa ();
        mpa.setId (1);
        mpa.setName ("G");
        if (mpaStorage.getWithId (1).isPresent ()) {
            Assertions.assertEquals (mpa, mpaStorage.getWithId (1).get ());
        }
        JSONObject jsonObject = new JSONObject (mapper.writeValueAsString (mpa));
        mvc.perform (get ("/mpa/1"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (jsonObject)))
                .andReturn ();
    }

    @DisplayName("Проверка на получение списка жанров")
    @Test
    public void getGenresTest () throws Exception {
        List<Genre> genres = new ArrayList<> ();
        genres.add (new Genre (1, "Комедия"));
        genres.add (new Genre (2, "Драма"));
        genres.add (new Genre (3, "Мультфильм"));
        genres.add (new Genre (4, "Триллер"));
        genres.add (new Genre (5, "Документальный"));
        genres.add (new Genre (6, "Боевик"));
        Assertions.assertTrue (genreStorage.findAll ().containsAll (genres));
        JSONArray usersArray = new JSONArray ();
        usersArray.put (new JSONObject (mapper.writeValueAsString (genres.get (0))));
        usersArray.put (new JSONObject (mapper.writeValueAsString (genres.get (1))));
        usersArray.put (new JSONObject (mapper.writeValueAsString (genres.get (2))));
        usersArray.put (new JSONObject (mapper.writeValueAsString (genres.get (3))));
        usersArray.put (new JSONObject (mapper.writeValueAsString (genres.get (4))));
        usersArray.put (new JSONObject (mapper.writeValueAsString (genres.get (5))));
        mvc.perform (get ("/genres"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (usersArray)))
                .andReturn ();

    }

    @DisplayName("Проверка на получение списка рейтингов")
    @Test
    public void getRatingsTest () throws Exception {
        List<Mpa> mpaList = new ArrayList<> ();
        mpaList.add (new Mpa (1, "G"));
        mpaList.add (new Mpa (2, "PG"));
        mpaList.add (new Mpa (3, "PG-13"));
        mpaList.add (new Mpa (4, "R"));
        mpaList.add (new Mpa (5, "NC-17"));
        Assertions.assertTrue (mpaStorage.getAll ().containsAll (mpaList));
        JSONArray usersArray = new JSONArray ();
        usersArray.put (new JSONObject (mapper.writeValueAsString (mpaList.get (0))));
        usersArray.put (new JSONObject (mapper.writeValueAsString (mpaList.get (1))));
        usersArray.put (new JSONObject (mapper.writeValueAsString (mpaList.get (2))));
        usersArray.put (new JSONObject (mapper.writeValueAsString (mpaList.get (3))));
        usersArray.put (new JSONObject (mapper.writeValueAsString (mpaList.get (4))));
        mvc.perform (get ("/mpa"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (usersArray)))
                .andReturn ();

    }

    @DisplayName("Проверка на корректное удаление фильма")
    @DirtiesContext
    @Test
    public void deleteFilmTest () throws Exception {
        postWithOkRequest (film, "/films");
        film.setId (1);
        Assertions.assertTrue (filmStorage.getFilms ().contains (film));
        mvc.perform (delete ("/films")
                        .contentType (MediaType.APPLICATION_JSON)
                        .content (mapper.writeValueAsBytes (film)))
                .andExpect (status ().isOk ())
                .andReturn ();
        Assertions.assertFalse (filmStorage.getFilms ().contains (film));
    }

    @DisplayName("Проверка на корректное удаление пользователя")
    @DirtiesContext
    @Test
    public void deleteUserTest () throws Exception {
        postWithOkRequest (user, "/users");
        user.setId (1);
        Assertions.assertTrue (userStorage.getUsers ().contains (user));
        mvc.perform (delete ("/users")
                        .contentType (MediaType.APPLICATION_JSON)
                        .content (mapper.writeValueAsBytes (user)))
                .andExpect (status ().isOk ())
                .andReturn ();
        Assertions.assertFalse (userStorage.getUsers ().contains (user));
    }

    @DisplayName("Проверка на корректное добавление лайка фильму")
    @DirtiesContext
    @Test
    public void addLikeTest () throws Exception {
        postWithOkRequest (film, "/films");
        postWithOkRequest (user, "/users");
        film.setId (1);
        user.setId (1);
        mvc.perform (put ("/films/1/like/1"))
                .andExpect (status ().isOk ())
                .andReturn ();
        Assertions.assertEquals (1, filmStorage.getFilmWithId (1).getLikes ().size ());
    }

    @DisplayName("Проверка на корректное удаление лайка фильму")
    @DirtiesContext
    @Test
    public void removeLikeTest () throws Exception {
        postWithOkRequest (film, "/films");
        postWithOkRequest (user, "/users");
        film.setId (1);
        user.setId (1);
        mvc.perform (put ("/films/1/like/1"))
                .andExpect (status ().isOk ())
                .andReturn ();
        Assertions.assertEquals (1, filmStorage.getFilmWithId (1).getLikes ().size ());
        mvc.perform (delete ("/films/1/like/1"))
                .andExpect (status ().isOk ())
                .andReturn ();
        Assertions.assertTrue (filmStorage.getFilmWithId (1).getLikes ().isEmpty ());
    }

    @DisplayName("Проверка на корректное получение списка популярных фильмов")
    @DirtiesContext
    @Test
    public void getPopularFilmsTest () throws Exception {
        postWithOkRequest (film, "/films");
        postWithOkRequest (film, "/films");
        postWithOkRequest (user, "/users");
        postWithOkRequest (user, "/users");
        postWithOkRequest (user, "/users");
        mvc.perform (put ("/films/1/like/1"))
                .andExpect (status ().isOk ())
                .andReturn ();
        mvc.perform (put ("/films/1/like/2"))
                .andExpect (status ().isOk ())
                .andReturn ();
        mvc.perform (put ("/films/2/like/3"))
                .andExpect (status ().isOk ())
                .andReturn ();
        JSONArray popularFilms = new JSONArray ();
        popularFilms.put (new JSONObject (mapper.writeValueAsString (filmStorage.getMostPopular (2).get (0))));
        popularFilms.put (new JSONObject (mapper.writeValueAsString (filmStorage.getMostPopular (2).get (1))));
        mvc.perform (get ("/films/popular"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (popularFilms)))
                .andReturn ();
    }

    @DisplayName("Проверка на корректное добавление и удаление друга пользователя")
    @DirtiesContext
    @Test
    public void addAndRemoveFriendTest() throws Exception{
        postWithOkRequest (user, "/users");
        postWithOkRequest (user, "/users");
        mvc.perform (put ("/users/1/friends/2"))
                .andExpect (status ().isOk ())
                .andReturn ();
        Assertions.assertTrue (userStorage.getUserWithId (1).getFriends ().contains (2));
        mvc.perform (delete ("/users/1/friends/2"))
                .andExpect (status ().isOk ())
                .andReturn ();
        Assertions.assertFalse (userStorage.getUserWithId (1).getFriends ().contains (2));
    }

    @DisplayName("Проверка на корректное получения списка друзей пользователя")
    @DirtiesContext
    @Test
    public void getFriendsTest() throws Exception{
        postWithOkRequest (user, "/users");
        postWithOkRequest (user, "/users");
        postWithOkRequest (user, "/users");
        mvc.perform (put ("/users/1/friends/2"))
                .andExpect (status ().isOk ())
                .andReturn ();
        mvc.perform (put ("/users/1/friends/3"))
                .andExpect (status ().isOk ())
                .andReturn ();
        Assertions.assertTrue (userStorage.getUserWithId (1).getFriends ().contains (2));
        Assertions.assertTrue (userStorage.getUserWithId (1).getFriends ().contains (3));
        JSONArray friends = new JSONArray (mapper.writeValueAsString (userStorage.getUserFriends (1)));
        mvc.perform (get ("/users/1/friends"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (friends)))
                .andReturn ();


    }

    @DisplayName("Проверка на корректное получения списка друзей друг друга")
    @DirtiesContext
    @Test
    public void getCrossFriendsTest() throws Exception{
        postWithOkRequest (user, "/users");
        postWithOkRequest (user, "/users");
        mvc.perform (put ("/users/1/friends/2"))
                .andExpect (status ().isOk ())
                .andReturn ();
        mvc.perform (put ("/users/2/friends/1"))
                .andExpect (status ().isOk ())
                .andReturn ();
        Assertions.assertTrue (userStorage.getUserWithId (1).getFriends ().contains (2));
        Assertions.assertTrue (userStorage.getUserWithId (2).getFriends ().contains (1));
        JSONArray friends = new JSONArray (mapper.writeValueAsString (userStorage.getUserCrossFriends (1, 2)));
        mvc.perform (get ("/users/1/friends/common/2"))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON_UTF8))
                .andExpect (content ().json (String.valueOf (friends)))
                .andReturn ();

    }

    //Отправка Post запроса с ожиданием кода 200
    private <T> void postWithOkRequest (T object, String path) throws Exception {
        mvc.perform (post (path)
                        .contentType (MediaType.APPLICATION_JSON)
                        .content (mapper.writeValueAsBytes (object)))
                .andExpect (status ().isOk ())
                .andReturn ();
    }

    //Отправка Post запроса с ожиданием 400-500 кодов ошибок
    private <T> void postWithBadRequest (T object, String path) throws Exception {
        mvc.perform (post (path)
                        .contentType (MediaType.APPLICATION_JSON)
                        .content (mapper.writeValueAsBytes (object)))
                .andExpect (status ().is4xxClientError ())
                .andReturn ();
    }

    //Отправка Put запроса с ожиданием кода 200
    private <T> void putWithOkRequest (T object, String path) throws Exception {
        mvc.perform (put (path)
                        .contentType (MediaType.APPLICATION_JSON)
                        .content (mapper.writeValueAsBytes (object)))
                .andExpect (status ().isOk ())
                .andReturn ();
    }

    //Отправка Put запроса с ожиданием 400-500 кодов ошибок
    private <T> void putWithBadRequest (T object, String path) throws Exception {
        mvc.perform (put (path)
                        .contentType (MediaType.APPLICATION_JSON)
                        .content (mapper.writeValueAsBytes (object)))
                .andExpect (status ().is4xxClientError ())
                .andReturn ();
    }

}
