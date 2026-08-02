package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 0;

    @Override
    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        users.get(userId).getFriends().add(friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        users.get(userId).getFriends().remove(Integer.valueOf(friendId));
    }

    @Override
    public List<Integer> getFriends(int userId) {
        return new ArrayList<>(users.get(userId).getFriends());
    }

    private int getNextId() {
        return ++currentId;
    }
}