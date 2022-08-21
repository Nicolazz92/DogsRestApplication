package com.example.dogsrestapplication;

import com.example.dogsrestapplication.model.Dog;
import io.restassured.module.mockmvc.response.MockMvcResponse;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

public final class TestUtils {

    public static MockMvcResponse postNewDog(Dog newDog, String url) {
        return given()
                .contentType("application/json")
                .body(newDog)
                .post(url);
    }

    public static MockMvcResponse putDog(Dog dog, String url) {
        return given()
                .contentType("application/json")
                .body(dog)
                .put(url);
    }

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

    public static Dog newNonValidatedDogFactoryMethod() {
        Dog nonValidatedDog = new Dog();
        nonValidatedDog.setName("x");
        nonValidatedDog.setHeight(1000);
        nonValidatedDog.setWeight(1000);
        LocalDate dateOfBirth = LocalDate.now().plusDays(20);
        nonValidatedDog.setDateOfBirth(dateOfBirth);
        return nonValidatedDog;
    }
}