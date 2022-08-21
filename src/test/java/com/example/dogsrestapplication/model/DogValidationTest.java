package com.example.dogsrestapplication.model;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static com.example.dogsrestapplication.TestUtils.newDogFactoryMethod;
import static com.example.dogsrestapplication.TestUtils.newNonValidatedDogFactoryMethod;
import static org.testng.Assert.assertTrue;

@Test
public class DogValidationTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void correctDogTest() {
        Dog correctDog = newDogFactoryMethod();
        Set<ConstraintViolation<Dog>> validate = validator.validate(correctDog);
        assertTrue(validate.isEmpty());
    }

    @Test
    public void incorrectDogTest() {
        Dog incorrectDog = newNonValidatedDogFactoryMethod();
        LocalDate dateOfBirth = incorrectDog.getDateOfBirth();

        Set<ConstraintViolation<Dog>> validate = validator.validate(incorrectDog);

        assertTrue(validate.stream().anyMatch(v -> "name size 'x' is not between 2 and 50".equals(v.getMessage())));
        assertTrue(validate.stream().anyMatch(v -> "height '1000' is greater then 200".equals(v.getMessage())));
        assertTrue(validate.stream().anyMatch(v -> "weight '1000' is greater then 100".equals(v.getMessage())));
        assertTrue(validate.stream().anyMatch(v -> String.format("dateOfBirth '%s' is future", dateOfBirth).equals(v.getMessage())));
    }
}
