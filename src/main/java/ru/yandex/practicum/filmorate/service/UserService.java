package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("Запрос на получение списка всех пользователей получен");
        return userStorage.findAll();
    }

    public User findById(int id) {
        log.info("Запрос на получение пользователя с id = {}", id);
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    public User create(User user) {
        userValidation(user);
        User created = userStorage.create(user);
        log.info("Пользователь с id = {} успешно добавлен", created.getId());
        return created;
    }

    public User update(User newUser) {
        userStorage.findById(newUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден"));

        userValidation(newUser);
        User updated = userStorage.update(newUser);
        log.info("Пользователь с id = {} успешно обновлен", updated.getId());
        return updated;
    }

    public void delete(int id) {
        userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));

        userStorage.delete(id);
        log.info("Пользователь с id = {} успешно удалён", id);
    }

    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException("Пользователь не может добавить самого себя в друзья");
        }

        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));

        userStorage.addFriend(userId, friendId);
        userStorage.addFriend(friendId, userId);
        log.info("Пользователи с id = {} и id = {} теперь друзья", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + friendId + " не найден"));

        userStorage.removeFriend(userId, friendId);
        userStorage.removeFriend(friendId, userId);
        log.info("Пользователи с id = {} и id = {} больше не друзья", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        log.info("Запрос на получение списка друзей пользователя с id = {}", userId);

        return userStorage.getFriends(userId).stream()
                .map(id -> userStorage.findById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден")))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId1, int userId2) {
        log.info("Запрос на получение общих друзей пользователей с id = {} и id = {}", userId1, userId2);

        Set<Integer> friends1 = Set.copyOf(userStorage.getFriends(userId1));
        List<Integer> friends2 = userStorage.getFriends(userId2);

        List<Integer> commonIds = friends2.stream()
                .filter(friends1::contains)
                .collect(Collectors.toList());

        List<User> commonUsers = new ArrayList<>();
        for (Integer id : commonIds) {
            commonUsers.add(userStorage.findById(id)
                    .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден")));
        }
        return commonUsers;
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
}
