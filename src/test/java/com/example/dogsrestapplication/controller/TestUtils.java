package com.example.dogsrestapplication.controller;

import com.example.dogsrestapplication.model.Dog;

import java.time.LocalDate;

public class TestUtils {

    public static Dog newDogFactoryMethod() {
        Dog newDog = new Dog();
        newDog.setName("Pippin");
        newDog.setDateOfBirth(LocalDate.of(2020, 2, 20));
        newDog.setHeight(50);
        newDog.setWeight(27);
        return newDog;
    }
}
