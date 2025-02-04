package com.evertea.AdvancedWeatherApp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class WeatherData implements Serializable {

    @Id
    private int Id;

    private String city;
    private ZonedDateTime dateTime;
    private double temp;
    private long relativeHumidity;
    private String dayNight;
    private double precipitation;
    private double rain;
    private double windSpeed;
    private String windDirection;

}
