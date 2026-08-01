package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;


public interface FilmStorage {
    Collection<Film> findAll();

    Film findById(int id);

    Film create(Film film);

    Film update(Film newFilm);

    void delete(int id);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);
}
