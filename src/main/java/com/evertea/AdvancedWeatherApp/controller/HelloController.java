package com.evertea.AdvancedWeatherApp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/home")
    public String greet(){
        System.out.println("greeting method called");
        return "Hello";
    }
}
