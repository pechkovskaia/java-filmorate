package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {

    // тесты: почта пустая или без @, логин пуст и содержит пробелы, имя не указано - должно занятся логином, дата рождения в будущем, тест когда все заполнено правильно,
    private UserController userController;

    @BeforeEach
    void setUp() {
        // собираем всю цепочку зависимостей вручную,
        // так как Spring в юнит-тесте не создаёт бины сам
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        User user = new User();
        user.setEmail("");
        user.setLogin("DariaDaria");
        user.setName("Daria");
        user.setBirthday(LocalDate.of(1997, 8, 9));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldThrowExceptionWhenNoSymbol() {
        User user = new User();
        user.setEmail("dariaD.mail.ru");
        user.setLogin("DariaDaria");
        user.setName("Daria");
        user.setBirthday(LocalDate.of(1997, 8, 9));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldPassIfEverythingCorrect() {
        User user = new User();
        user.setEmail("dariap@mail.ru");
        user.setLogin("DariaDaria");
        user.setName("Daria");
        user.setBirthday(LocalDate.of(1997, 8, 9));

        assertDoesNotThrow(() -> userController.create(user));
    }

    @Test
    void shouldThrowExceptionWhenLoginIBlank() {
        User user = new User();
        user.setEmail("dariaD@mail.ru");
        user.setLogin("");
        user.setName("Daria");
        user.setBirthday(LocalDate.of(1997, 8, 9));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldThrowExceptionWhenLoginHasSpace() {
        User user = new User();
        user.setEmail("dariaD.mail.ru");
        user.setLogin("Daria Daria");
        user.setName("Daria");
        user.setBirthday(LocalDate.of(1997, 8, 9));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldTakeLoginAsName() {
        User user = new User();
        user.setEmail("dariap@mail.ru");
        user.setLogin("Daria");
        user.setName("");
        user.setBirthday(LocalDate.of(1997, 8, 9));

        User createdUser = userController.create(user);

        assertEquals("Daria", createdUser.getName());
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsTomorrow() {
        User user = new User();
        user.setEmail("dariap@mail.ru");
        user.setLogin("DariaD");
        user.setName("Daria");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldPassWhenBirthdayIsToday() {
        User user = new User();
        user.setEmail("dariap@mail.ru");
        user.setLogin("DariaD");
        user.setName("Daria");
        user.setBirthday(LocalDate.now());

        assertDoesNotThrow(() -> userController.create(user));
    }
}

