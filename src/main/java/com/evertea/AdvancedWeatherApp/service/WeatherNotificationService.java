package com.evertea.AdvancedWeatherApp.service;

import com.evertea.AdvancedWeatherApp.DTO.WeatherData;
import com.evertea.AdvancedWeatherApp.DTO.WeatherNotification;
import com.evertea.AdvancedWeatherApp.repo.NotificationRepo;
import com.evertea.AdvancedWeatherApp.webSockets.WeatherDataWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class WeatherNotificationService {

    @Autowired
    private NotificationRepo notificationRepo;
    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @Autowired
    private WeatherDataWebSocketHandler webSocketHandler;

    public void getNotificationMessage(WeatherData weatherData) {

        int notificationId = 0;
        int[] notificationIdArray = new int[15];

        if(weatherData.getTempMax() > 35){
            System.out.println("1");
            notificationId = 1;
            notificationIdArray[0] = 1;
        }if(weatherData.getTempMin() < 20){
            System.out.println(weatherData.getTempMin());
            System.out.println("2");
            notificationId = 2;
            notificationIdArray[1] = 2;
        }if(weatherData.getRainSum() >= 20){
            System.out.println("3");
            notificationId = 3;
            notificationIdArray[2] = 3;
        }if(weatherData.getRainSum() >= 5){
            System.out.println("4");
            notificationId = 4;
            notificationIdArray[3] = 4;
        }if(weatherData.getRainSum() == 0){
            System.out.println("5");
            notificationId = 5;
            notificationIdArray[4] = 5;
        }if(weatherData.getWindSpeedMax() >= 30) {
            System.out.println("6");
            notificationId = 6;
            notificationIdArray[5] = 6;
        }if(weatherData.getWindSpeedMax() >= 10){
            System.out.println("7");
            notificationId = 7;
            notificationIdArray[6] = 7;
        }if(weatherData.getWindSpeedMax() < 10){
            System.out.println("8");
            notificationId = 8;
            notificationIdArray[7] = 8;
        }if(weatherData.getUvIndexMax() > 8){
            System.out.println("9");
            notificationId = 9;
            notificationIdArray[8] = 9;
        }if(weatherData.getSunShine() < 5){
            System.out.println("10");
            notificationId = 10;
            notificationIdArray[9] = 10;
        }if(weatherData.getPrecipitationSum() > 50){
            System.out.println("12");
            notificationIdArray[10] = 12;
        }if(weatherData.getPrecipitationSum() >= 20 && weatherData.getPrecipitationSum() <= 50){
            System.out.println("13");
            notificationIdArray[11] = 13;
        }if(weatherData.getPrecipitationSum() >= 5 && weatherData.getPrecipitationSum() <= 20){
            System.out.println("14");
            notificationIdArray[12] = 14;
        }if(weatherData.getPrecipitationSum() >= 1 && weatherData.getPrecipitationSum() <= 5){
            System.out.println("15");
            notificationIdArray[13] = 15;
        }if(weatherData.getPrecipitationSum() == 0){
            System.out.println("16");
            notificationIdArray[14] = 16;
        }

        if(notificationId != 0){

            System.out.println("-----------------Weather Notifications-----------------");

            for(int i=0; i < notificationIdArray.length; i++){
                if(notificationIdArray[i] != 0){
                    WeatherNotification notification = notificationRepo.findById(notificationIdArray[i]);
                    System.out.println("* "+notification.getMessage());
                    firebaseMessagingService.sendNotificationByToken(notification.getMessage());
                    System.out.println();

                    notifyWeatherDataWebSocket(notification.getMessage(), "notifications");
                    //formatMessage(notification.getMessage(), weatherData);
                }

            }

            //Notification notification = notificationRepo.findById(notificationId);
//            WeatherNotification notification = notificationRepo.findById(notificationId);
//
//            System.out.println();
//            System.out.println("-----------------Weather Notifications-----------------");
//            System.out.println();
//            System.out.println(notification.getMessage());
//            System.out.println();
//            System.out.println();


        }

    }
//    private String formatMessage(String message, WeatherData weatherData) throws NullPointException {
//
//        if(weatherData.getCity() == null){
//            throw new NullPointException("City cannot be null");
//        }
//
//        // replace placeholders with actual data
//        return message.replace("{temp}", String.valueOf(weatherData.getTempMax()))
//                .replace("{rain}", String.valueOf(weatherData.getRainSum()))
//                .replace("{wind}", String.valueOf(weatherData.getWindSpeedMax()))
//                .replace("{sunShine}", String.valueOf(weatherData.getSunShine()));
//
//
//    }

    private void notifyWeatherDataWebSocket(String data, String type){
        try{
            String message = data
                    .replace("\\", "\\\\")  // Escape backslashes
                    .replace("\"", "\\\"")  // Escape double quotes
                    .replace("\n", "\\n")   // Escape newlines
                    .replace("\r", "\\r")   // Escape carriage returns
                    .replace("\t", "\\t");  // Escape tabs

            String messageJson = String.format(
                    "{\"type\": \"%s\", \"message\": \"%s\"}",
                    type,
                    message
            );

            webSocketHandler.broadCast(messageJson);
            System.out.println("Weather notification sent: "+ messageJson);

        }catch(Exception e){
            System.out.println("Error while notifying log webSocket: "+ e.getMessage());
        }
    }
}
