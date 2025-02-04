package com.evertea.AdvancedWeatherApp.controller;

import com.evertea.AdvancedWeatherApp.model.Configuration;
import com.evertea.AdvancedWeatherApp.main.Weather;
import com.evertea.AdvancedWeatherApp.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    @Autowired
    private Weather weather;

    @PostMapping("/demo")
    public WeatherData getCity(@RequestBody WeatherData data){
        System.out.println("Fetched the city");
        weather.getCity(data);

        return weather.getAllData();
    }

}
