package com.example.dogsrestapplication.controller;

import com.example.dogsrestapplication.exception.DogNotFoundException;
import com.example.dogsrestapplication.model.Dog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static com.example.dogsrestapplication.controller.TestUtils.newDogFactoryMethod;
import static org.testng.Assert.*;

@ContextConfiguration("/applicationContext.xml")
public class DogsControllerUnitTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private DogsController controller;

    @Test
    public void posting_dogs_adds_them_to_dogs_data() {
        int startSize = controller.getAll().size();
        controller.post(newDogFactoryMethod());
        Dog dog = newDogFactoryMethod();
        dog.setId("1");
        controller.post(dog);
        assertEquals(2, controller.getAll().size() - startSize);
    }

    @Test
    public void posting_dog_sets_id_and_getting_dog_by_id() {
        Dog dog = newDogFactoryMethod();
        dog.setName("Funfirik");
        controller.post(dog);
        assertEquals(dog.getName(), controller.get(dog.getId()).getName());
    }

    @Test(expectedExceptions = DogNotFoundException.class)
    public void getting_dog_by_unexcisting_id_produces_exception() {
        controller.get("unexcisting_id");
    }

    @Test
    public void putting_dog_replaces_posted_dog() {
        int startSize = controller.getAll().size();
        Dog dog = newDogFactoryMethod();
        controller.post(dog);
        assertEquals(1, controller.getAll().size() - startSize);

        dog.setName("Funfirik");
        controller.put(dog.getId(), dog);
        assertEquals(1, controller.getAll().size() - startSize);
        assertEquals("Funfirik", controller.get(dog.getId()).getName());
    }

    @Test(expectedExceptions = DogNotFoundException.class)
    public void putting_dog_by_unexcisting_id_produces_exception() {
        controller.put("unexcisting_id", newDogFactoryMethod());
    }

    @Test
    public void posting_dog_and_deleting_dog() {
        int startSize = controller.getAll().size();
        Dog dog = newDogFactoryMethod();
        controller.post(dog);
        assertEquals(1, controller.getAll().size() - startSize);

        controller.delete(dog.getId());
        assertEquals(startSize, controller.getAll().size());
    }

    @Test(expectedExceptions = DogNotFoundException.class)
    public void deleting_dog_by_unexcisting_id_produces_exception() {
        controller.delete("unexcisting_id");
    }
}