package com.example.dogsrestapplication.exception;

public class DogNotFoundException extends RuntimeException {

    public DogNotFoundException(String id) {
        super(id);
    }

    public String getId() {
        return getMessage();
    }
}
