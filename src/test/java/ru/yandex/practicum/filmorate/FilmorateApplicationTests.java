package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {

    private User user;
    private Film film;

    private final String USERS_PATH = "/users";
    private final String FILMS_PATH = "/films";

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mvc;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    public void reloadModels () {
        user = new User ();
        user.setId (1);
        user.setEmail ("user@gmail.com");
        user.setName ("after");
        user.setLogin ("aft");
        user.setBirthday (LocalDate.of (1980, 1, 1));
        film = new Film ();
        film.setId (1);
        film.setName ("Film");
        film.setDescription ("Film description about film");
        film.setReleaseDate (LocalDate.now ().minusDays (1));
        film.setDuration (2600);
    }

    @DisplayName("Проверка на создание валидного пользователя")
    @DirtiesContext
    @Test
    public void createCorrectUserTest () throws Exception {
        User user1 = new User ();
        user1.setId (3);
        user1.setEmail ("user@gmail.com");
        user1.setName ("after");
        user1.setLogin ("aft");
        user1.setBirthday (LocalDate.of (1980, 1, 1));
        postWithOkRequest (user1, USERS_PATH);
    }

    @DisplayName("Проверка на создание пользователя с null email")
    @Test
    public void createEmptyEmailUserTest () throws Exception {
        user.setEmail (null);
        postWithBadRequest (user, USERS_PATH);
    }

    @DisplayName("Проверка на создание пользователя с пустым email")
    @Test
    public void createBlankEmailUserTest () throws Exception {
        user.setEmail (" ");
        postWithBadRequest (user, USERS_PATH);
    }

    @DisplayName("Проверка на создание пользователя с некорректным email")
    @Test
    public void createNonEmailUserTest () throws Exception {
        user.setEmail ("userGmail.ru");
        postWithBadRequest (user, USERS_PATH);
    }

    @DisplayName("Проверка на создание пользователя с пустым логином")
    @Test
    public void createEmptyLoginUserTest () throws Exception {
        user.setLogin ("");
        postWithBadRequest (user, USERS_PATH);
    }

    @DisplayName("Проверка на создание пользователя с пустым логином состоящим из пробелов")
    @Test
    public void createBlankLoginUserTest () throws Exception {
        user.setLogin (" ");
        postWithBadRequest (user, USERS_PATH);
    }

    @DisplayName("Проверка на создание пользователя с пробелами в логине")
    @Test
    public void createLoginWithSpacesUserTest () throws Exception {
        user.setLogin ("user login");
        postWithBadRequest (user, USERS_PATH);
    }

    @DisplayName("Проверка на создание пользователя с днем рождения в будущем")
    @Test
    public void createFutureBirthdayUserTest () throws Exception {
        user.setBirthday (LocalDate.now ().plusDays (1));
        postWithBadRequest (user, USERS_PATH);
    }

    @DisplayName("Проверка на создание корректного фильма")
    @DirtiesContext
    @Test
    public void createCorrectFilmTest () throws Exception {
        postWithOkRequest (film, FILMS_PATH);
    }

    @DisplayName("Проверка на создание фильма без названия")
    @Test
    public void createEmptyNameFilmTest () throws Exception {
        film.setName (null);
        postWithBadRequest (film, FILMS_PATH);
    }

    @DisplayName("Проверка на создание фильма с пустым названием")
    @Test
    public void createBlankNameFilmTest () throws Exception {
        film.setName (" ");
        postWithBadRequest (film, FILMS_PATH);
    }

    @DisplayName("Проверка на создание фильма с описанием длиннее 200 символов")
    @Test
    public void createOutOfSizeDescriptionFilmTest () throws Exception {
        film.setDescription ("e".repeat (204));
        postWithBadRequest (film, FILMS_PATH);
    }

    @DisplayName("Проверка на создание фильма до даты выхода первого фильма")
    @Test
    public void createOutOfReleaseDateFilmTest () throws Exception {
        film.setReleaseDate (LocalDate.of (1895, 12, 27));
        postWithBadRequest (film, FILMS_PATH);
    }

    @DisplayName("Проверка на создание фильма с отрицательной продолжительностью")
    @Test
    public void createNegativeDurationFilmTest () throws Exception {
        film.setDuration (-100);
        postWithBadRequest (film, FILMS_PATH);
    }

    @DisplayName("Проверка на получение списка пользователей")
    @Test
    public void getUsersTest () throws Exception {
        try {
            postWithOkRequest (user, USERS_PATH);
        } catch (ValidationException e) {
            System.out.println (e.getMessage ());
        } catch (Exception e) {
            System.out.println ("Пользователь с таким ИД был создан ранее");
        }
        JSONArray usersArray = new JSONArray ();
        usersArray.put (new JSONObject (mapper.writeValueAsString (user)));
        mvc.perform (get (USERS_PATH))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON))
                .andExpect (content ().json (String.valueOf (usersArray)))
                .andReturn ();

    }

    @DisplayName("Проверка на получение списка фильмов")
    @Test
    public void getFilmsTest () throws Exception {
        try {
            postWithOkRequest (film, FILMS_PATH);
        } catch (ValidationException e) {
            System.out.println (e.getMessage ());
        } catch (Exception e) {
            System.out.println ("Фильм с таким ИД был создан ранее");
        }
        JSONArray filmsArray = new JSONArray ();
        filmsArray.put (new JSONObject (mapper.writeValueAsString (film)));
        mvc.perform (get (FILMS_PATH))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON))
                .andExpect (content ().json (String.valueOf (filmsArray)))
                .andReturn ();

    }

    @DisplayName("Проверка на корректное редактирование фильма с существующим ИД")
    @DirtiesContext
    @Test
    public void putCorrectFilmTest () throws Exception {
        try {
            postWithOkRequest (film, FILMS_PATH);
        } catch (ValidationException e) {
            System.out.println (e.getMessage ());
        } catch (Exception e) {
            System.out.println ("Фильм с таким ИД был создан ранее");
        }
        film.setDescription ("updatedDesc");
        putWithOkRequest (film, FILMS_PATH);
        JSONArray filmsArray = new JSONArray ();
        filmsArray.put (new JSONObject (mapper.writeValueAsString (film)));
        mvc.perform (get (FILMS_PATH))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON))
                .andExpect (content ().json (String.valueOf (filmsArray)))
                .andReturn ();
    }

    @DisplayName("Проверка на корректное редактирование фильма с несуществующим ИД")
    @Test
    public void putFilmWithIncorrectIdTest () {
        try {
            postWithOkRequest (film, FILMS_PATH);
        } catch (ValidationException e) {
            System.out.println (e.getMessage ());
        } catch (Exception e) {
            System.out.println ("Фильм с таким ИД был создан ранее");
        }
        film.setDescription ("updatedDesc");
        film.setId (10);

        NestedServletException exception = assertThrows (NestedServletException.class, () ->
                putWithBadRequest (film, FILMS_PATH));

        String expectedMessage = "Произошла ошибка при обновлении фильма, введите корректные данные";
        String actualMessage = exception.getMessage ();
        if (actualMessage != null) {
            assertTrue (actualMessage.contains (expectedMessage));
        }
    }

    @DisplayName("Проверка на корректное редактирование пользователя с существующим ИД")
    @DirtiesContext
    @Test
    public void putCorrectUserTest () throws Exception {
        try {
            postWithOkRequest (user, USERS_PATH);
        } catch (ValidationException e) {
            System.out.println (e.getMessage ());
        } catch (Exception e) {
            System.out.println ("Пользователь с таким ИД был создан ранее");
        }
        user.setName ("updatedName");
        putWithOkRequest (user, USERS_PATH);
        JSONArray usersArray = new JSONArray ();
        usersArray.put (new JSONObject (mapper.writeValueAsString (user)));
        mvc.perform (get (USERS_PATH))
                .andExpect (status ().isOk ())
                .andExpect (content ().contentType (MediaType.APPLICATION_JSON))
                .andExpect (content ().json (String.valueOf (usersArray)))
                .andReturn ();
    }

    @DisplayName("Проверка на корректное редактирование пользователя с несуществующим ИД")
    @Test
    public void putUserWithIncorrectIdTest () {
        try {
            postWithOkRequest (user, USERS_PATH);
        } catch (ValidationException e) {
            System.out.println (e.getMessage ());
        } catch (Exception e) {
            System.out.println ("Фильм с таким ИД был создан ранее");
        }
        user.setName ("updatedName");
        user.setId (10);

        NestedServletException exception = assertThrows (NestedServletException.class, () ->
                putWithBadRequest (user, USERS_PATH));

        String expectedMessage = "Произошла ошибка при обновлении пользователя, введите корректные данные";
        String actualMessage = exception.getMessage ();
        if (actualMessage != null) {
            assertTrue (actualMessage.contains (expectedMessage));
        }
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
                .andExpect (status ().isBadRequest ())
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
                .andExpect (status ().isBadRequest ())
                .andReturn ();
    }

}
