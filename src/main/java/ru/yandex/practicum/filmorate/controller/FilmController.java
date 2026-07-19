package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

   @PostMapping
   public Film create(@RequestBody Film film) {
        filmValidation(film);

       film.setId(getNextId());
       films.put(film.getId(), film);
       log.info("Фильм с id = {} успешно добавлен", film.getId());
       return film;
   }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        filmValidation(newFilm);

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Фильм с таким id = {} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        films.put(newFilm.getId(), newFilm);
        log.info("Фильм с id = {} успешно обновлен", newFilm.getId());
        return newFilm;
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
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата выпуска фильма должна быть не раньше 28/12/1895");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
