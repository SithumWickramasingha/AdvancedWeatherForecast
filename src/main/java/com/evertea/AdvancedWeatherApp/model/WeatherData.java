package com.evertea.AdvancedWeatherApp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private String dateTime;
    private String cloudCover;
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
