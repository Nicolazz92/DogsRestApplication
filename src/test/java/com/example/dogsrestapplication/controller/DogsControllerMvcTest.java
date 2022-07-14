package com.example.dogsrestapplication.controller;

import com.example.dogsrestapplication.model.Dog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.example.dogsrestapplication.controller.TestUtils.newDogFactoryMethod;
import static com.example.dogsrestapplication.controller.TestUtils.newNonValidatedDogFactoryMethod;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@WebAppConfiguration
@ContextConfiguration(locations = "/test-context.xml")
public class DogsControllerMvcTest extends AbstractTestNGSpringContextTests {

    private static final String URL = "/dogs";
    private static final List<String> SKIPPED_ID_FIELD = Collections.singletonList("id");

    @Autowired
    private WebApplicationContext webApplicationContext;

    MockMvc mockMvc;
    ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @BeforeMethod
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.requestSpecification = new MockMvcRequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void full_dog_livecycle() throws UnsupportedEncodingException, JsonProcessingException {
        //POST_200
        Dog newDog = newDogFactoryMethod();
        String createdDogAsString = given()
                .contentType("application/json")
                .body(newDog)
                .post(URL)
                .then().status(HttpStatus.CREATED)
                .extract().response().mvcResult().getResponse().getContentAsString();

        //PUT_200
        Dog createdDog = objectMapper.readValue(createdDogAsString, Dog.class);
        String dogId = createdDog.getId();
        Dog anotherNewDog = newDogFactoryMethod();
        given()
                .contentType("application/json")
                .body(anotherNewDog)
                .put(String.join("/", URL, dogId))
                .then().status(HttpStatus.CREATED);

        //GET_200
        String getResponse = get(String.join("/", URL, dogId))
                .then().status(HttpStatus.OK)
                .extract().response().mvcResult().getResponse().getContentAsString();
        Dog dog = objectMapper.readValue(getResponse, Dog.class);
        assertNotNull(dog);
        assertTrue(reflectionEquals(dog, anotherNewDog, SKIPPED_ID_FIELD));

        //DELETE_200
        delete(String.join("/", URL, dogId)).then().status(HttpStatus.OK);

        //GET_404
        delete(String.join("/", URL, dogId)).then().status(HttpStatus.NOT_FOUND);
    }

    @Test
    public void post_dog_and_find_it_in_getall_response() throws JsonProcessingException, UnsupportedEncodingException {
        Dog newDog = newDogFactoryMethod();
        given()
                .contentType("application/json")
                .body(newDog)
                .post(URL)
                .then()
                .status(HttpStatus.CREATED);

        String getAllResponse = get(URL)
                .then()
                .contentType("application/json;charset=UTF-8")
                .status(HttpStatus.OK)
                .extract().response().mvcResult().getResponse().getContentAsString();
        List<Dog> content = objectMapper.readValue(getAllResponse, new TypeReference<>() {
        });
        assertTrue(content.stream()
                .anyMatch(d -> reflectionEquals(d, newDog, SKIPPED_ID_FIELD)));
    }

    @Test
    public void putting_unexisting_dog_returns_404() {
        given()
                .contentType("application/json")
                .body(newDogFactoryMethod())
                .put(URL + "/1")
                .then().status(HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleting_unexisting_dog_returns_404() {
        delete(URL + "/1").then().status(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getting_unexisting_dog_returns_404() {
        get(URL + "/1").then().status(HttpStatus.NOT_FOUND);
    }

    @Test
    public void dog_validation_test_restassured() {
        Dog badDogRequest = newNonValidatedDogFactoryMethod();
        LocalDate dateOfBirth = badDogRequest.getDateOfBirth();

        given()
                .contentType("application/json")
                .body(badDogRequest)
                .post(URL)
                .then()
                .status(HttpStatus.BAD_REQUEST)
                .statusLine(containsString("name size 'x' is not between 2 and 50"))
                .statusLine(containsString("height '1000' is greater then 200"))
                .statusLine(containsString("weight '1000' is greater then 100"))
                .statusLine(containsString(String.format("dateOfBirth '%s' is future", dateOfBirth)));
    }

    @Test
    public void dog_validation_test_mockmvc() throws Exception {
        Dog badDogRequest = newNonValidatedDogFactoryMethod();
        LocalDate dateOfBirth = badDogRequest.getDateOfBirth();

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badDogRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("name size 'x' is not between 2 and 50")))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("height '1000' is greater then 200")))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("weight '1000' is greater then 100")))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains(String.format("dateOfBirth '%s' is future", dateOfBirth))));
    }
}