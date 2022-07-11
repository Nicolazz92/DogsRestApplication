package com.example.dogsrestapplication.controller;

import com.example.dogsrestapplication.model.Dog;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

public class TestUtils {

    public static Dog newDogFactoryMethod() {
        Dog newDog = new Dog();
        newDog.setName(UUID.randomUUID().toString());
        LocalDate now = LocalDate.now();
        newDog.setDateOfBirth(LocalDate.of(
                new Random().nextInt(now.getYear() - 2010) + 2010,
                new Random().nextInt(now.getMonthValue() - 1) + 1,
                new Random().nextInt(27) + 1
        ));
        newDog.setHeight(new Random().nextInt(199) + 1);
        newDog.setWeight(new Random().nextInt(100));
        return newDog;
    }
}
