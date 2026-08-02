package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        log.info("Запрос на получение списка всех фильмов получен");
        return filmStorage.findAll();
    }

    public Film findById(int id) {
        log.info("Запрос на получение фильма с id = {}", id);
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    public Film create(Film film) {
        filmValidation(film);
        Film created = filmStorage.create(film);
        log.info("Фильм с id = {} успешно добавлен", created.getId());
        return created;
    }

    public Film update(Film newFilm) {
        filmStorage.findById(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + newFilm.getId() + "не найден"));

        filmValidation(newFilm);
        Film updated = filmStorage.update(newFilm);
        log.info("Фильм с id = {} успешно обновлен", updated.getId());
        return updated;
    }

    public void delete(int id) {
        filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));

        filmStorage.delete(id);
        log.info("Фильм с id = {} успешно удалён", id);
    }

    public void addLike(int filmId, int userId) {
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        filmStorage.addLike(filmId, userId);
        log.info("Пользователь с id = {} поставил лайк фильму с id = {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        filmStorage.removeLike(filmId, userId);
        log.info("Пользователь с id = {} удалил лайк с фильма с id = {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            log.warn("Параметр count должен быть больше нуля, передано: {}", count);
            throw new ValidationException("Количество фильмов должно быть больше нуля");
        }
        log.info("Запрос на получение {} самых популярных фильмов", count);
        return filmStorage.getPopularFilms(count);
    }

    private void filmValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Фильм должен иметь название");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Вы превысили максимальную длину описания: 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null) {
            log.warn("Дата выхода фильма не может быть пустой");
            throw new ValidationException("Дата выхода фильма не может быть пустой");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата выпуска фильма должна быть не раньше 28/12/1895");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}