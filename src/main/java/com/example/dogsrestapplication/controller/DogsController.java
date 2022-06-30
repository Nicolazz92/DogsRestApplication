package com.example.dogsrestapplication.controller;

import com.example.dogsrestapplication.exception.DogNotFoundException;
import com.example.dogsrestapplication.model.Dog;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/dogs")
public class DogsController {
    private static final Map<String, Dog> DOGS = new ConcurrentHashMap<>();

    @PostMapping
    public void post(@RequestBody Dog newDog) {
        newDog.setId(UUID.randomUUID().toString());
        DOGS.put(newDog.getId(), newDog);
    }

    @GetMapping()
    public Collection<Dog> getAll() {
        return DOGS.values();
    }

    @GetMapping("{id}")
    public Dog get(@PathVariable String id) {
        if (DOGS.containsKey(id)) {
            return DOGS.get(id);
        } else {
            throw new DogNotFoundException(id);
        }
    }

    @PutMapping("{id}")
    public void put(@PathVariable String id, @RequestBody Dog newDog) {
        if (DOGS.containsKey(id)) {
            newDog.setId(DOGS.get(id).getId());
            DOGS.put(id, newDog);
        } else {
            throw new DogNotFoundException(id);
        }
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        if (DOGS.containsKey(id)) {
            DOGS.remove(id);
        } else {
            throw new DogNotFoundException(id);
        }
    }
}
