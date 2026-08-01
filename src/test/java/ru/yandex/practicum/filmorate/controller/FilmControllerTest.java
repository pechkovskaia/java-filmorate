package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldPassIfEverythingCorrect() {
        Film film = new Film();
        film.setName("Дьявол носит Prada");
        film.setDescription("Начинающая журналистка Энди устраивается младшей помощницей к Миранде Пристли");
        film.setReleaseDate(LocalDate.of(2006, 6, 22));
        film.setDuration(109);

        assertDoesNotThrow(() -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionTooLong() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("а".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldPassWhenDescriptionIsExactly200() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("а".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertDoesNotThrow(() -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateTooEarly() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // на день раньше границы
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldPassWhenReleaseDateIRightOnTheEdge() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(120);

        assertDoesNotThrow(() -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsZero() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        Film film = new Film();
        film.setName("Название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-10);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }
}
