package com.example.dogsrestapplication.controller;

import com.example.dogsrestapplication.model.Dog;
import com.example.dogsrestapplication.service.DogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/dogs")
public class DogsController {

    private DogsService service;

    @PostMapping
    public void post(@RequestBody Dog newDog) {
        service.create(newDog);
    }

    @GetMapping()
    public Collection<Dog> getAll() {
        return service.getAll();
    }

    @GetMapping("{id}")
    public Dog get(@PathVariable String id) {
        return service.get(id);
    }

    @PutMapping("{id}")
    public void put(@PathVariable String id, @RequestBody Dog newDog) {
        service.replace(id, newDog);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @Autowired
    public void setService(DogsService service) {
        this.service = service;
    }
}
