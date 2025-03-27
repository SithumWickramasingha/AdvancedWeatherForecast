package com.evertea.AdvancedWeatherApp.controller;

import com.evertea.AdvancedWeatherApp.DTO.LocationAndTokenReceiver;
import com.evertea.AdvancedWeatherApp.service.FirebaseMessagingService;
import com.evertea.AdvancedWeatherApp.service.WeatherService;
import com.evertea.AdvancedWeatherApp.DTO.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class WeatherController {

    @Autowired
    private WeatherService weather;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @PostMapping("/demo")
    public List<WeatherData> getCity(@RequestBody LocationAndTokenReceiver firebaseNotification){
        System.out.println("Fetched the city");

        if(firebaseNotification == null){
            System.out.println("no data found");
            return null;
        }

        weather.getCity(firebaseNotification);

        firebaseMessagingService.getTokenFromController(firebaseNotification);

        // retrieve weather data for the given city
        return weather.getWeatherData(firebaseNotification.getCity());
    }



}
