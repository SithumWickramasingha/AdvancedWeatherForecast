package com.evertea.AdvancedWeatherApp.DTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    private String city;
    private String dateTime;
    private double temp;
    private int relativeHumidity;
    private double precipitation;
    private double rain;
    private String weatherCode;
    private double windSpeed;
    private String windDirection;
    private double soilTemp;




}
