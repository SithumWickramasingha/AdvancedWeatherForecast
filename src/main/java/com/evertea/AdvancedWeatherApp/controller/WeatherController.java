package com.evertea.AdvancedWeatherApp.controller;

import com.evertea.AdvancedWeatherApp.service.WeatherService;
import com.evertea.AdvancedWeatherApp.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WeatherController {

    @Autowired
    private WeatherService weather;

    @PostMapping("/demo")
    public List<WeatherData> getCity(@RequestBody WeatherData data){
        System.out.println("Fetched the city");
        weather.getCity(data);

        // retrieve weather data for the given city
        return weather.getWeatherData(data.getCity());
    }

}
