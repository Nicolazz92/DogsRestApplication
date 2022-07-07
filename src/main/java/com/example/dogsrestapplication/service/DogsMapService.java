package com.example.dogsrestapplication.service;

import com.example.dogsrestapplication.exception.DogNotFoundException;
import com.example.dogsrestapplication.model.Dog;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DogsMapService implements DogsService {
    private static final Map<String, Dog> DOGS = new ConcurrentHashMap<>();

    public void create(Dog newDog) {
        newDog.setId(UUID.randomUUID().toString());
        DOGS.put(newDog.getId(), newDog);
    }

    public Collection<Dog> getAll() {
        return DOGS.values();
    }

    public Dog get(String id) throws DogNotFoundException {
        if (DOGS.containsKey(id)) {
            return DOGS.get(id);
        } else {
            throw new DogNotFoundException(id);
        }
    }

    public void replace(String id, Dog newDog) throws DogNotFoundException {
        if (DOGS.containsKey(id)) {
            newDog.setId(DOGS.get(id).getId());
            DOGS.put(id, newDog);
        } else {
            throw new DogNotFoundException(id);
        }
    }

    public void delete(String id) throws DogNotFoundException {
        if (DOGS.containsKey(id)) {
            DOGS.remove(id);
        } else {
            throw new DogNotFoundException(id);
        }
    }
}
