package com.example.dogsrestapplication.controller;

import com.example.dogsrestapplication.service.DogsMapService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;

import static com.example.dogsrestapplication.controller.TestUtils.newDogFactoryMethod;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.get;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;

//@WebAppConfiguration
@ContextConfiguration(locations = "/test-context.xml")
public class DogsControllerMvcTest extends AbstractTestNGSpringContextTests {

    private static final String URL = "/dogs";

//    @Autowired
//    private WebApplicationContext webApplicationContext;

    @Autowired
    private DogsController dogsController;

    @Autowired
    private DogsMapService dogsMapService;

    @BeforeMethod
    public void before() {
//        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(dogsController).build();
        dogsController.setService(dogsMapService);
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.requestSpecification = new MockMvcRequestSpecBuilder().setContentType(ContentType.JSON).build();

    }

    @Test
    public void posting_dogs_adds_them_to_dogs_data() throws UnsupportedEncodingException {
        dogsMapService.create(newDogFactoryMethod());
        System.out.println(get(URL).mvcResult().getResponse().getContentType());
        System.out.println(get(URL).mvcResult().getResponse().getContentAsString());
    }
}