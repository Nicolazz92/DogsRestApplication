package com.example.dogsrestapplication.controller;

import com.example.dogsrestapplication.exception.DogNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class DogsControllerAdvice {

    @ResponseBody
    @ExceptionHandler(DogNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RestErrorMessage dogNotFoundExceptionHandler(DogNotFoundException exception) {
        return new RestErrorMessage(String.format("dog %s not found", exception.getId()));
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestErrorMessage illegalArgumentExceptionHandler(IllegalArgumentException exception) {
        return new RestErrorMessage(exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestErrorMessage illegalArgumentExceptionHandler(MethodArgumentNotValidException exception) {
        return new RestErrorMessage(exception.getMessage());
    }

    public static class RestErrorMessage {
        public String message;

        public RestErrorMessage(String message) {
            this.message = message;
        }
    }
}
