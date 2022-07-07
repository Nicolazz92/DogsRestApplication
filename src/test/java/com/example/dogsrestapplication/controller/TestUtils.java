package com.example.dogsrestapplication.controller;

import com.example.dogsrestapplication.model.Dog;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

public class TestUtils {

    public static Dog newDogFactoryMethod() {
        Dog newDog = new Dog();
        newDog.setName(UUID.randomUUID().toString());
        newDog.setDateOfBirth(LocalDate.of(
                new Random().nextInt(2050),
                new Random().nextInt(11) + 1,
                new Random().nextInt(27) + 1
        ));
        newDog.setHeight(new Random().nextInt(100));
        newDog.setWeight(new Random().nextInt(100));
        return newDog;
    }
}
