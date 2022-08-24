package com.example.dogsrestapplication.service;

import com.example.dogsrestapplication.dao.JdbcDogDao;
import com.example.dogsrestapplication.exception.DogNotFoundException;
import com.example.dogsrestapplication.model.Dog;

import java.util.Collection;
import java.util.UUID;

public class DogsJdbcService implements DogsService {

    private final JdbcDogDao dogDao;

    public DogsJdbcService(JdbcDogDao dogDao) {
        this.dogDao = dogDao;
    }

    @Override
    public Dog create(Dog newDog) {
        newDog.setId(UUID.randomUUID().toString());
        return dogDao.create(newDog).orElse(null);
    }

    @Override
    public Collection<Dog> getAll() {
        return dogDao.getAll();
    }

    @Override
    public Dog get(String id) throws DogNotFoundException {
        return dogDao.get(id).orElseThrow(() -> new DogNotFoundException(id));
    }

    @Override
    public Dog replace(String id, Dog newDog) throws DogNotFoundException {
        return dogDao.replace(id, newDog).orElseThrow(() -> new DogNotFoundException(id));
    }

    @Override
    public void delete(String id) throws DogNotFoundException {
        if (!dogDao.delete(id)) {
            throw new DogNotFoundException(id);
        }
    }
}
