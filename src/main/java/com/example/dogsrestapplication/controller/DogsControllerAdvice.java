package com.example.dogsrestapplication.controller;

import com.example.dogsrestapplication.exception.DogNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DogsControllerAdvice {

    @ResponseBody
    @ExceptionHandler(DogNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String dogNotFoundExceptionHandler(DogNotFoundException exception) {
        return String.format("dog %s not found", exception.getId());
    }
}
