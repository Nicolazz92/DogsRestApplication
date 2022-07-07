package com.example.dogsrestapplication.service;

import com.example.dogsrestapplication.exception.DogNotFoundException;
import com.example.dogsrestapplication.model.Dog;

import java.util.Collection;

public interface DogsService {

    void create(Dog newDog);

    Collection<Dog> getAll();

    Dog get(String id) throws DogNotFoundException;

    void replace(String id, Dog newDog) throws DogNotFoundException;

    void delete(String id) throws DogNotFoundException;
}
