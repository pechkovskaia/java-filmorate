package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 0;

    @Override
    public Collection<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(int id) {
        Film film = films.get(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return film;
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public void delete(int id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        films.remove(id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        Set<Integer> likes = film.getLikes();
        likes.add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }

        Set<Integer> likes = film.getLikes();
        likes.remove(Integer.valueOf(userId));
    }

    private int getNextId() {
        return ++currentId;
    }
}
