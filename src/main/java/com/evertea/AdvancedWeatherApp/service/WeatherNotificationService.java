package com.evertea.AdvancedWeatherApp.service;

import com.evertea.AdvancedWeatherApp.model.WeatherData;
import com.evertea.AdvancedWeatherApp.model.WeatherNotification;
import com.evertea.AdvancedWeatherApp.repo.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class WeatherNotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    public void getNotificationMessage(WeatherData weatherData){

        int notificationId = 0;

        if(weatherData.getTempMax() > 35){
            System.out.println("1");
            notificationId = 1;
        } else if(weatherData.getTempMin() < 20){
            System.out.println(weatherData.getTempMin());
            System.out.println("2");
            notificationId = 2;
        }else if(weatherData.getRainSum() >= 20){
            System.out.println("3");
            notificationId = 3;
        }else if(weatherData.getRainSum() >= 5){
            System.out.println("4");
            notificationId = 4;
        }else if(weatherData.getRainSum() == 0){
            System.out.println("5");
            notificationId = 5;
        }else if(weatherData.getWindSpeedMax() >= 30){
            System.out.println("6");
            notificationId = 6;
        }else if(weatherData.getWindSpeedMax() >= 10){
            System.out.println("7");
            notificationId = 7;
        }else if(weatherData.getWindSpeedMax() < 10){
            System.out.println("8");
            notificationId = 8;
        }else if(weatherData.getUvIndexMax() > 8){
            System.out.println("9");
            notificationId = 9;
        }else if(weatherData.getSunShine() < 5){
            System.out.println("10");
            notificationId = 10;
        }else{
            System.out.println("11");
            notificationId = 11;
        }

        if(notificationId != 0){
            System.out.println("notification id called");
            System.out.println("notification id: "+notificationId);
            //Notification notification = notificationRepo.findById(notificationId);
            WeatherNotification notification = notificationRepo.findById(notificationId);

            System.out.println();
            System.out.println("-----------------Weather Notifications-----------------");
            System.out.println();
            System.out.println(notification.getMessage());
            System.out.println();
            System.out.println();

            formatMessage(notification.getMessage(), weatherData);
        }

    }
    private String formatMessage(String message, WeatherData weatherData){

        // replace placeholders with actual data
        return message.replace("{temp}", String.valueOf(weatherData.getTempMax()))
                .replace("{rain}", String.valueOf(weatherData.getRainSum()))
                .replace("{wind}", String.valueOf(weatherData.getWindSpeedMax()))
                .replace("{sunShine}", String.valueOf(weatherData.getSunShine()));


    }
}
