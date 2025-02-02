package com.evertea.AdvancedWeatherApp.main;

import com.evertea.AdvancedWeatherApp.config.Configuration;
import org.springframework.stereotype.Component;

@Component
public class Weather {

    public Configuration getCity(Configuration config){

        Configuration configuration = new Configuration(config.getCity());

        System.out.println(configuration.getCity());

        return configuration;
    }
}
