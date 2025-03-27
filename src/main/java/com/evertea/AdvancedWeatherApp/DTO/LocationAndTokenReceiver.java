package com.evertea.AdvancedWeatherApp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class LocationAndTokenReceiver {

    private double latitude;
    private double longitude;
    private String fcmToken;
}
