package com.example.dogsrestapplication.controller;

import com.example.dogsrestapplication.model.Dog;
import com.example.dogsrestapplication.service.DogsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/dogs")
public class DogsController {

    private final DogsService service;

    public DogsController(DogsService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Dog post(@RequestBody @Valid Dog newDog) {
        return service.create(newDog);
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
    @ResponseStatus(HttpStatus.CREATED)
    public Dog put(@PathVariable String id, @RequestBody @Valid Dog newDog) {
        return service.replace(id, newDog);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
