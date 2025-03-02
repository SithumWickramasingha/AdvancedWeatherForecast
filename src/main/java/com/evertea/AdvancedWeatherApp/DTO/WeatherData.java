package com.evertea.AdvancedWeatherApp.DTO;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class WeatherData implements Serializable {

    @Id
    private int Id;

    private String city;
    private String dateTime;
    private String cloudCover;
    private double currentTemp;
    private double tempMax;
    private double tempMin;
    private long dayLight;
    private long sunShine;
    private double uvIndexMax;
    private double precipitationSum;
    private double rainSum;
    private double windSpeedMax;
    private String windDirection;



}
