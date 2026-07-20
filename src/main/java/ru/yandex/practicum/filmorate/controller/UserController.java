package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// создание пользователя;
//обновление пользователя;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение списка всех пользователей получен");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        userValidation(user);

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с таким id = {} успешно добавлен", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        userValidation(newUser);

        if (!users.containsKey(newUser.getId())) {
            log.warn("Пользователь с таким id = {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        users.put(newUser.getId(), newUser);
        log.info("Пользователь с таким id = {} успешно обновлен", newUser.getId());
        return newUser;
    }

    private void userValidation(User user) {
            if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                log.warn("Название почты не должно быть пустым и должно содержать @");
                throw new ValidationException("Почта не заполнена");
            }
            if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                log.warn("Логин не должен быть пуст и не должен содержать пробелы");
                throw new ValidationException("Логин не может быть пуст или содержать пробелы");
            }
            if (user.getBirthday() == null) {
                log.warn("Дата дня рождения не может быть пустой");
                throw new ValidationException("Дата дня рождения не может быть пустой");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                log.info("Имя не указано, будет использован логин: {}", user.getLogin());
                user.setName(user.getLogin());
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.warn("Дата рождения не может быть в будущем");
                throw new ValidationException("День Рождения не может быть в будущем");
            }
        }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}