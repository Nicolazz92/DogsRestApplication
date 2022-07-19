package com.example.dogsrestapplication.service;

import com.example.dogsrestapplication.exception.DogNotFoundException;
import com.example.dogsrestapplication.model.Dog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import static com.example.dogsrestapplication.TestUtils.newDogFactoryMethod;
import static org.testng.Assert.assertEquals;

@ContextConfiguration("/test-context.xml")
public class DogsMapServiceUnitTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private DogsMapService dogsMapService;

    @Test
    public void posting_dogs_adds_them_to_dogs_data() {
        int startSize = dogsMapService.getAll().size();
        dogsMapService.create(newDogFactoryMethod());
        Dog dog = newDogFactoryMethod();
        dog.setId("1");
        dogsMapService.create(dog);
        assertEquals(2, dogsMapService.getAll().size() - startSize);
    }

    @Test
    public void posting_dog_sets_id_and_getting_dog_by_id() {
        Dog dog = newDogFactoryMethod();
        dog.setName("Funfirik");
        dogsMapService.create(dog);
        assertEquals(dog.getName(), dogsMapService.get(dog.getId()).getName());
    }

    @Test(expectedExceptions = DogNotFoundException.class)
    public void getting_dog_by_unexcisting_id_produces_exception() {
        dogsMapService.get("unexcisting_id");
    }

    @Test
    public void putting_dog_replaces_posted_dog() {
        int startSize = dogsMapService.getAll().size();
        Dog dog = newDogFactoryMethod();
        dogsMapService.create(dog);
        assertEquals(1, dogsMapService.getAll().size() - startSize);
        String dogId = dog.getId();

        dog = newDogFactoryMethod();
        dogsMapService.replace(dogId, dog);
        assertEquals(1, dogsMapService.getAll().size() - startSize);
        ReflectionAssert.assertReflectionEquals(dog, dogsMapService.get(dogId));
    }

    @Test(expectedExceptions = DogNotFoundException.class)
    public void putting_dog_by_unexcisting_id_produces_exception() {
        dogsMapService.replace("unexcisting_id", newDogFactoryMethod());
    }

    @Test
    public void posting_dog_and_deleting_dog() {
        int startSize = dogsMapService.getAll().size();
        Dog dog = newDogFactoryMethod();
        dogsMapService.create(dog);
        assertEquals(1, dogsMapService.getAll().size() - startSize);

        dogsMapService.delete(dog.getId());
        assertEquals(startSize, dogsMapService.getAll().size());
    }

    @Test(expectedExceptions = DogNotFoundException.class)
    public void deleting_dog_by_unexcisting_id_produces_exception() {
        dogsMapService.delete("unexcisting_id");
    }
}