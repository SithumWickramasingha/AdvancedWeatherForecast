package com.evertea.AdvancedWeatherApp.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Entity
@Component
public class Configuration implements Serializable {
    @Id
    private int id;
    private String city;

    public Configuration(String city){
        this.city = city;
    }

    public Configuration(){}

    public String getCity(){
        return city;
    }

    public void setCity(String city){
        this.city = city;
    }
}
