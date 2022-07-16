package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    @GetMapping("{id}")
    public Optional<Mpa> getMpaWithId(@PathVariable Integer id) {
        if (id < 1) {
            throw new NotFoundException("Не найден рейтинг");
        }
        return mpaService.getWithId(id);
    }

    @GetMapping()
    public List<Mpa> getAll() {
        return mpaService.getAll();
    }
}