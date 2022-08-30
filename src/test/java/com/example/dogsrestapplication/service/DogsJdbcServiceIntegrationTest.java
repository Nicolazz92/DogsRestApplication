package com.example.dogsrestapplication.service;

import com.example.dogsrestapplication.exception.DogNotFoundException;
import com.example.dogsrestapplication.model.Dog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import static com.example.dogsrestapplication.TestUtils.SKIPPED_ID_FIELD;
import static com.example.dogsrestapplication.TestUtils.newDogFactoryMethod;
import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@ContextConfiguration("/test-context.xml")
public class DogsJdbcServiceIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private DogsJdbcService dogsJdbcService;

    @Test
    public void posting_dogs_adds_them_to_dogs_data() {
        int startSize = dogsJdbcService.getAll().size();
        dogsJdbcService.create(newDogFactoryMethod());
        dogsJdbcService.create(newDogFactoryMethod());
        assertEquals(2, dogsJdbcService.getAll().size() - startSize);
    }

    @Test
    public void posting_dog_sets_id_and_getting_dog_by_id() {
        Dog dog = newDogFactoryMethod();
        dog.setName("Funfirik");
        dogsJdbcService.create(dog);
        assertEquals(dog.getName(), dogsJdbcService.get(dog.getId()).getName());
    }

    @Test(expectedExceptions = DogNotFoundException.class)
    public void getting_dog_by_unexcisting_id_produces_exception() {
        dogsJdbcService.get("unexcisting_id");
    }

    @Test
    public void putting_dog_replaces_posted_dog() {
        int startSize = dogsJdbcService.getAll().size();
        Dog dog = newDogFactoryMethod();
        dogsJdbcService.create(dog);
        assertEquals(1, dogsJdbcService.getAll().size() - startSize);
        String dogId = dog.getId();

        dog = newDogFactoryMethod();
        dogsJdbcService.replace(dogId, dog);
        assertEquals(1, dogsJdbcService.getAll().size() - startSize);
        assertTrue(reflectionEquals(dog, dogsJdbcService.get(dogId), SKIPPED_ID_FIELD));

    }

    @Test(expectedExceptions = DogNotFoundException.class)
    public void putting_dog_by_unexcisting_id_produces_exception() {
        dogsJdbcService.replace("unexcisting_id", newDogFactoryMethod());
    }

    @Test
    public void posting_dog_and_deleting_dog() {
        int startSize = dogsJdbcService.getAll().size();
        Dog dog = newDogFactoryMethod();
        dogsJdbcService.create(dog);
        assertEquals(1, dogsJdbcService.getAll().size() - startSize);

        dogsJdbcService.delete(dog.getId());
        assertEquals(startSize, dogsJdbcService.getAll().size());
    }

    @Test(expectedExceptions = DogNotFoundException.class)
    public void deleting_dog_by_unexcisting_id_produces_exception() {
        dogsJdbcService.delete("unexcisting_id");
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ".*Data truncation: Data too long for column 'name' at row 1*")
    public void database_constraint_dog_name_works() {
        Dog dog = newDogFactoryMethod();
        dog.setName("dogdogdogdogdogdogdogdogdogdogdogdogdogdogdogdogdo");
        dogsJdbcService.create(dog);
        ReflectionAssert.assertReflectionEquals(dog, dogsJdbcService.get(dog.getId()));

        dog = newDogFactoryMethod();
        dog.setName("dogdogdogdogdogdogdogdogdogdogdogdogdogdogdogdogdodogdogdogdogdogdogdogdogdogdogdogdogdogdogdogdogdo");
        dogsJdbcService.create(dog);
    }
}