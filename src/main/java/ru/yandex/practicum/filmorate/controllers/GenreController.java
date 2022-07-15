package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService service;

    @Autowired
    public GenreController (GenreService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public Optional<Genre> getGenre (@PathVariable Integer id) {
        if (id < 1) {
            throw new NotFoundException ("Жанр не найден");
        }
        return service.findById (id);
    }


    @GetMapping
    public Collection<Genre> findAll () {
        return service.findAll ();
    }

}