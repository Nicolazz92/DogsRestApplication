package com.example.dogsrestapplication.controller;

import com.example.dogsrestapplication.model.Dog;
import com.example.dogsrestapplication.service.DogsMapService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.dogsrestapplication.controller.TestUtils.newDogFactoryMethod;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.apache.commons.lang.builder.EqualsBuilder.reflectionEquals;
import static org.junit.Assert.*;

@WebAppConfiguration
@ContextConfiguration(locations = "/test-context.xml")
public class DogsControllerMvcTest extends AbstractTestNGSpringContextTests {

    private static final String URL = "/dogs";
    private static final List<String> ID_FIELD = Collections.singletonList("id");

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DogsController dogsController;

    @Autowired
    private DogsMapService dogsMapService;

    ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @BeforeMethod
    public void before() {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(dogsController).build();
        dogsController.setService(dogsMapService);
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.requestSpecification = new MockMvcRequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void post_dog_and_find_it_in_getall_response() throws UnsupportedEncodingException, JsonProcessingException {
        Dog newDog = newDogFactoryMethod();
        MockHttpServletResponse postResponse = given()
                .contentType("application/json")
                .body(newDog)
                .post(URL)
                .getMvcResult()
                .getResponse();
        assertEquals(200, postResponse.getStatus());

        MockHttpServletResponse getAllResponse = get(URL).mvcResult().getResponse();
        assertEquals("application/json;charset=UTF-8", getAllResponse.getContentType());
        assertEquals(200, getAllResponse.getStatus());
        List<Dog> content = objectMapper.readValue(getAllResponse.getContentAsString(), new TypeReference<>() {});
        assertTrue(content.stream()
                .anyMatch(d -> reflectionEquals(d, newDog, ID_FIELD)));
    }

    @Test
    public void put_unexisting_dog() {
        Dog newDog = newDogFactoryMethod();
        MockHttpServletResponse postResponse = given()
                .contentType("application/json")
                .body(newDog)
                .put(URL + "/1")
                .getMvcResult()
                .getResponse();
        assertEquals(404, postResponse.getStatus());
    }

    @Test
    public void delete_unexisting_dog() {
        MockHttpServletResponse postResponse = delete(URL + "/1")
                .getMvcResult()
                .getResponse();
        assertEquals(404, postResponse.getStatus());
    }

    @Test
    public void get_unexisting_dog() {
        MockHttpServletResponse postResponse = get(URL + "/1")
                .getMvcResult()
                .getResponse();
        assertEquals(404, postResponse.getStatus());
    }

    @Test
    public void full_dog_livecycle() throws UnsupportedEncodingException, JsonProcessingException {
        //POST
        Dog newDog = newDogFactoryMethod();
        MockHttpServletResponse postResponse = given()
                .contentType("application/json")
                .body(newDog)
                .post(URL)
                .getMvcResult()
                .getResponse();
        assertEquals(200, postResponse.getStatus());

        //GET_ALL
        MockHttpServletResponse getAllResponse = get(URL).mvcResult().getResponse();
        assertEquals(200, getAllResponse.getStatus());
        List<Dog> dogList = objectMapper.readValue(getAllResponse.getContentAsString(), new TypeReference<>() {});
        Optional<Dog> optionalDog = dogList.stream()
                .filter(d -> reflectionEquals(d, newDog, ID_FIELD))
                .findAny();
        assertTrue(optionalDog.isPresent());

        //PUT
        String dogId = optionalDog.get().getId();
        Dog anotherNewDog = newDogFactoryMethod();
        MockHttpServletResponse putResponse = given()
                .contentType("application/json")
                .body(anotherNewDog)
                .put(String.join("/", URL, dogId))
                .getMvcResult()
                .getResponse();
        assertEquals(200, putResponse.getStatus());

        //GET
        MockHttpServletResponse getResponse = get(String.join("/", URL, dogId))
                .getMvcResult()
                .getResponse();
        assertEquals(200, getResponse.getStatus());
        Dog dog = objectMapper.readValue(getResponse.getContentAsString(), Dog.class);
        assertNotNull(dog);
        assertTrue(reflectionEquals(dog, anotherNewDog, ID_FIELD));

        //DELETE
        MockHttpServletResponse deleteResponse = delete(String.join("/", URL, dogId))
                .getMvcResult()
                .getResponse();
        assertEquals(200, deleteResponse.getStatus());

        //GET
        MockHttpServletResponse getUnexistsResponse = delete(String.join("/", URL, dogId))
                .getMvcResult()
                .getResponse();
        assertEquals(404, getUnexistsResponse.getStatus());
    }
}