package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.Collection;

@Service
public interface FilmAndUserService<T> {

     T create (T t) throws ValidationException;

     T put (T t) throws ValidationException;

     void delete (T t) throws NotFoundException;

     Collection<T> findAll ();

     T getWithId (int id) throws NotFoundException;

     boolean validation (T t);
}
