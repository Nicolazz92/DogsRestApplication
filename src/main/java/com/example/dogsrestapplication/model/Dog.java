package com.example.dogsrestapplication.model;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class Dog {
    private String id;

    @NotBlank
    @Size(min = 2, max = 50, message = "name size '${validatedValue}' is not between {min} and {max}")
    private String name;

    @PastOrPresent(message = "dateOfBirth '${validatedValue}' is future")
    private LocalDate dateOfBirth;

    @DecimalMin(value = "1", message = "height '${validatedValue}' is less then {value}")
    @DecimalMax(value = "200", message = "height '${validatedValue}' is greater then {value}")
    private int height;

    @DecimalMin(value = "0", message = "weight '${validatedValue}' is less then {value}")
    @DecimalMax(value = "100", message = "weight '${validatedValue}' is greater then {value}")
    private int weight;

    private String code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", height=" + height +
                ", weight=" + weight +
                ", code=" + code +
                '}';
    }
}
