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
    private WeatherService service;

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @PostMapping("/location")
    public void locationAndTokenReceiver(@RequestBody LocationAndTokenReceiver receiver){

        System.out.println("Token: "+receiver.getFcmToken());

        // weatherService class
        service.retrieveLocation(receiver);

        service.getLocationName(receiver);


        firebaseMessagingService.getTokenFromController(receiver);


    }


}
