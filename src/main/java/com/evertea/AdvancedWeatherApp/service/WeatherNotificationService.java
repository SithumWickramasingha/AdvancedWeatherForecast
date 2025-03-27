package com.evertea.AdvancedWeatherApp.service;

import com.evertea.AdvancedWeatherApp.DTO.WeatherData;
import com.evertea.AdvancedWeatherApp.DTO.WeatherNotification;
import com.evertea.AdvancedWeatherApp.repo.FindNotification;
import com.evertea.AdvancedWeatherApp.webSockets.WeatherDataWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;


@Service
public class WeatherNotificationService {

    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @Autowired
    private FindNotification findNotification;

    public void getNotificationMessage(List<List<Object>> weatherDataList, int month){

        String message;
        String[][] messageQueue = new String[16][2];
        String[][] messageQueueRH = new String[16][2];
        String[][] messageQueueRP = new String[16][2];
        String[][] messageQueueWS = new String[16][2];
        String[][] messageQueueST = new String[16][2];
        String[][] messageQueueAC = new String[16][2];


        for(int i =0; i<=15; i++){

            int temp = ((Number) weatherDataList.get(i).get(3)).intValue();
            int relativeHumidity = ((Number) weatherDataList.get(i).get(4)).intValue();
            int precipitation = ((Number) weatherDataList.get(i).get(5)).intValue();
            int rain = ((Number) weatherDataList.get(i).get(6)).intValue();
            //int weatherCode = ((Number) weatherDataList.get(i).get(7)).intValue();
            int windSpeed = ((Number) weatherDataList.get(i).get(8)).intValue();
            //int windDirection = ((Number) weatherDataList.get(i).get(9)).intValue();
            int soilTemp = ((Number) weatherDataList.get(i).get(10)).intValue();
            String time = weatherDataList.get(i).get(2).toString().substring(0,2) + weatherDataList.get(i).get(2).toString().substring(5);

            if(temp > 15){
                messageQueue[i][0] = time;
                messageQueue[i][1] = "1";
                //System.out.println(message);
            }
            if(temp >= 30 && temp <= 35){
                messageQueue[i][0] = time;
                messageQueue[i][1] = "2";
                //System.out.println(message);
            }
            if(temp >= 25 && temp <= 30){
                messageQueue[i][0] = time;
                messageQueue[i][1] = "3";
                //System.out.println(message);
            }
            if(temp >= 20 && temp <= 25){
                messageQueue[i][0] = time;
                messageQueue[i][1] = "4";
                //System.out.println(message);
            }
            if(temp >= 15 && temp <= 20){
                messageQueue[i][0] = time;
                messageQueue[i][1] = "5";
                //System.out.println(message);
            }
            if(temp >= 10 && temp <= 15){
                messageQueue[i][0] = time;
                messageQueue[i][1] = "6";
                //System.out.println(message);
            }
            if(temp < 10){
                messageQueue[i][0] = time;
                messageQueue[i][1] = "7";
                //System.out.println(message);
            }
            if(relativeHumidity > 90){
                messageQueueRH[i][0] = time;
                messageQueueRH[i][1] = "8";
                //System.out.println(message);
            }
            if(relativeHumidity >= 85 && relativeHumidity <= 90){
                messageQueueRH[i][0] = time;
                messageQueueRH[i][1] = "9";
                //System.out.println(message);
            }
            if(relativeHumidity >= 65 && relativeHumidity < 85){
                messageQueueRH[i][0] = time;
                messageQueueRH[i][1] = "10";
                //System.out.println(message);
            }
            if(relativeHumidity >= 50 && relativeHumidity < 65){
                messageQueueRH[i][0] = time;
                messageQueueRH[i][1] = "11";
                //System.out.println(message);
            }
            if(relativeHumidity < 50){

                messageQueueRH[i][0] = time;
                messageQueueRH[i][1] = "12";
                //System.out.println(message);
            }
            // rain and precipitation conditions start
            if(rain > 20){
                messageQueueRP[i][0] = time;
                messageQueueRP[i][1] = "13";
                //System.out.println(message);
            }
            if(rain >= 10 && rain <= 20){
                messageQueueRP[i][0] = time;
                messageQueueRP[i][1] = "14";
                //System.out.println(message);
            }
            if(rain >= 5 && rain < 10){
                messageQueueRP[i][0] = time;
                messageQueueRP[i][1] = "15";
                //System.out.println(message);
            }
            if(rain < 5 && precipitation > 0){
                messageQueueRP[i][0] = time;
                messageQueueRP[i][1] = "16";
                //System.out.println(message);
            }
            if(rain == 0 && precipitation == 0){
                messageQueueRP[i][0] = time;
                messageQueueRP[i][1] = "17";
                //System.out.println(message);
            }
            if(windSpeed > 40){
                messageQueueWS[i][0] = time;
                messageQueueWS[i][1] = "18";
            }
            if(windSpeed >= 30 && windSpeed <=40){
                messageQueueWS[i][0] = time;
                messageQueueWS[i][1] = "19";
            }
            if(windSpeed >= 20 && windSpeed <30){
                messageQueueWS[i][0] = time;
                messageQueueWS[i][1] = "20";
            }
            if(windSpeed >= 10 && windSpeed < 20){
                messageQueueWS[i][0] = time;
                messageQueueWS[i][1] = "21";
            }
            if(windSpeed < 10){
                messageQueueWS[i][0] = time;
                messageQueueWS[i][1] = "22";
            }
            if(soilTemp > 35){
                message = "☀️ High soil temperature detected ("+soilTemp+"°C). Increase irrigation.";
                messageQueueST[i][0] = time;
                messageQueueST[i][1] = "23";
            }
            if(soilTemp >= 30 && soilTemp <= 35){
                message = "🔥 Warm soil conditions detected ("+soilTemp+"°C). Maintain hydration.";
                messageQueueST[i][0] = time;
                messageQueueST[i][1] = "24";
            }
            if(soilTemp >= 20 && soilTemp <= 30){
                message = "✅ Optimal soil temperature for tea plants at "+soilTemp+" °C.";
                messageQueueST[i][0] = time;
                messageQueueST[i][1] = "25";
            }
            if(soilTemp >= 15 && soilTemp < 20){
                message = "🌡️ Cool soil detected at "+soilTemp+" °C. Mulching can help retain warmth.";
                messageQueueST[i][0] = time;
                messageQueueST[i][1] = "26";
            }
            if(soilTemp < 15){
                message = "❄️ Low soil temperature detected ("+soilTemp+" °C). Protect root systems.";
                messageQueueST[i][0] = time;
                messageQueueST[i][1] = "27";
            }
            if(rain > 10 && windSpeed > 30){
                message = "⛈️ Severe weather alert! Heavy rain and strong winds expected at "+time+".";
                messageQueueAC[i][0] = time;
                messageQueueAC[i][1] = "28";
            }
            if(relativeHumidity > 85 && temp > 28){
                message = "🔥 High humidity and heat detected! Risk of plant diseases";
                messageQueueAC[i][0] = time;
                messageQueueAC[i][1] = "29";
            }

        }


        // customize current time to hour
        LocalTime truncatedTime = LocalTime.now().truncatedTo(ChronoUnit.HOURS);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh a");
        String formattedTime = truncatedTime.format(formatter);


        if(formattedTime.equals("10 PM") || formattedTime.equals("11 PM")) {
            HashSet<String> set = new HashSet<>();

            for (int i = 0; i < messageQueue.length; i++) {

                //System.out.println(messageQueue[i][0].equals("06 AM"));
                if (messageQueue[i][0].equals("06 AM") ||
                        messageQueue[i][0].equals("07 AM") ||
                        messageQueueRP[i][0].equals("06 AM") ||
                        messageQueueRP[i][0].equals("07 AM") ||
                        messageQueueRH[i][0].equals("06 AM") ||
                        messageQueueRH[i][0].equals("07 AM") ||
                        messageQueueWS[i][0].equals("06 AM") ||
                        messageQueueWS[i][0].equals("07 AM") ||
                        messageQueueST[i][0].equals("06 AM") ||
                        messageQueueST[i][0].equals("07 AM")

                ) {


                    set.add(messageQueue[i][1]);
                    set.add(messageQueueRP[i][1]);
                    set.add(messageQueueRH[i][1]);
                    set.add(messageQueueST[i][1]);
                    set.add(messageQueueWS[i][1]);
                }

            }


            sendNotificationToFrontend(month, set);

        }
        else if(formattedTime.equals("06 AM") || formattedTime.equals("07 AM")){

            HashSet<String> set = new HashSet<>();

            for(int i=0; i<messageQueue.length; i++){

                if( messageQueue[i][0].equals("08 AM") ||
                        messageQueue[i][0].equals("09 AM") ||
                        messageQueueRH[i][0].equals("08 AM") ||
                        messageQueueRH[i][0].equals("09 AM") ||
                        messageQueueRP[i][0].equals("08 AM") ||
                        messageQueueRP[i][0].equals("09 AM") ||
                        messageQueueWS[i][0].equals("08 AM") ||
                        messageQueueWS[i][0].equals("09 AM") ||
                        messageQueueST[i][0].equals("08 AM") ||
                        messageQueueST[i][0].equals("09 AM")
                ){

                    set.add(messageQueue[i][1]);
                    set.add(messageQueueRP[i][1]);
                    set.add(messageQueueRH[i][1]);
                    set.add(messageQueueST[i][1]);
                    set.add(messageQueueWS[i][1]);
                }

            }

            sendNotificationToFrontend(month, set);
        }else if(formattedTime.equals("08 AM") || formattedTime.equals("09 AM")){

            HashSet<String> set = new HashSet<>();

            for(int i=0; i< messageQueue.length; i++){
                if(
                        messageQueue[i][0].equals("10 PM") ||
                                messageQueue[i][0].equals("11 PM") ||
                                messageQueueRH[i][0].equals("10 PM") ||
                                messageQueueRH[i][0].equals("11 PM") ||
                                messageQueueRP[i][0].equals("10 PM") ||
                                messageQueueRP[i][0].equals("11 PM") ||
                                messageQueueWS[i][0].equals("10 PM") ||
                                messageQueueWS[i][0].equals("11 PM") ||
                                messageQueueST[i][0].equals("10 PM") ||
                                messageQueueST[i][0].equals("11 PM")
                ){
                    set.add(messageQueue[i][1]);
                    set.add(messageQueueRP[i][1]);
                    set.add(messageQueueRH[i][1]);
                    set.add(messageQueueST[i][1]);
                    set.add(messageQueueWS[i][1]);

                }
            }

            sendNotificationToFrontend(month, set);
        }else if(formattedTime.equals("10 AM") || formattedTime.equals("11 AM")){
            System.out.println("Time is :"+ formattedTime);
            HashSet<String> set = new HashSet<>();

            for(int i=0; i< messageQueue.length; i++){
                if(
                        messageQueue[i][0].equals("12 PM") ||
                                messageQueue[i][0].equals("01 PM") ||
                                messageQueueRH[i][0].equals("12 PM") ||
                                messageQueueRH[i][0].equals("01 PM") ||
                                messageQueueRP[i][0].equals("12 PM") ||
                                messageQueueRP[i][0].equals("01 PM") ||
                                messageQueueWS[i][0].equals("12 PM") ||
                                messageQueueWS[i][0].equals("01 PM") ||
                                messageQueueST[i][0].equals("12 PM") ||
                                messageQueueST[i][0].equals("01 PM")
                ){
                    set.add(messageQueue[i][1]);
                    set.add(messageQueueRP[i][1]);
                    set.add(messageQueueRH[i][1]);
                    set.add(messageQueueST[i][1]);
                    set.add(messageQueueWS[i][1]);

                }
            }

            sendNotificationToFrontend(month, set);
        }else if(formattedTime.equals("12 PM") || formattedTime.equals("01 PM")){
            HashSet<String> set = new HashSet<>();

            for(int i=0; i< messageQueue.length; i++){
                if(
                        messageQueue[i][0].equals("02 PM") ||
                                messageQueue[i][0].equals("03 PM") ||
                                messageQueueRH[i][0].equals("02 PM") ||
                                messageQueueRH[i][0].equals("03 PM") ||
                                messageQueueRP[i][0].equals("02 PM") ||
                                messageQueueRP[i][0].equals("03 PM") ||
                                messageQueueWS[i][0].equals("02 PM") ||
                                messageQueueWS[i][0].equals("03 PM") ||
                                messageQueueST[i][0].equals("02 PM") ||
                                messageQueueST[i][0].equals("03 PM")
                ){
                    set.add(messageQueue[i][1]);
                    set.add(messageQueueRP[i][1]);
                    set.add(messageQueueRH[i][1]);
                    set.add(messageQueueST[i][1]);
                    set.add(messageQueueWS[i][1]);

                }
            }

            sendNotificationToFrontend(month, set);


        }else if(formattedTime.equals("02 PM") || formattedTime.equals("03 PM")){
            HashSet<String> set = new HashSet<>();

            for(int i=0; i< messageQueue.length; i++){
                if(
                        messageQueue[i][0].equals("04 PM") ||
                                messageQueue[i][0].equals("05 PM") ||
                                messageQueueRH[i][0].equals("04 PM") ||
                                messageQueueRH[i][0].equals("05 PM") ||
                                messageQueueRP[i][0].equals("04 PM") ||
                                messageQueueRP[i][0].equals("05 PM") ||
                                messageQueueWS[i][0].equals("04 PM") ||
                                messageQueueWS[i][0].equals("05 PM") ||
                                messageQueueST[i][0].equals("04 PM") ||
                                messageQueueST[i][0].equals("05 PM")
                ){
                    set.add(messageQueue[i][1]);
                    set.add(messageQueueRP[i][1]);
                    set.add(messageQueueRH[i][1]);
                    set.add(messageQueueST[i][1]);
                    set.add(messageQueueWS[i][1]);
                }
            }

            sendNotificationToFrontend(month, set);

        }else if(formattedTime.equals("04 PM") || formattedTime.equals("05 PM")){
            HashSet<String> set = new HashSet<>();

            for(int i=0; i< messageQueue.length; i++){
                if(
                        messageQueue[i][0].equals("06 PM") ||
                                messageQueue[i][0].equals("07 PM") ||
                                messageQueueRH[i][0].equals("06 PM") ||
                                messageQueueRH[i][0].equals("07 PM") ||
                                messageQueueRP[i][0].equals("06 PM") ||
                                messageQueueRP[i][0].equals("07 PM") ||
                                messageQueueWS[i][0].equals("06 PM") ||
                                messageQueueWS[i][0].equals("07 PM") ||
                                messageQueueST[i][0].equals("06 PM") ||
                                messageQueueST[i][0].equals("07 PM")
                ){
                    set.add(messageQueue[i][1]);
                    set.add(messageQueueRP[i][1]);
                    set.add(messageQueueRH[i][1]);
                    set.add(messageQueueST[i][1]);
                    set.add(messageQueueWS[i][1]);
                }
            }

            sendNotificationToFrontend(month, set);

        }else if(formattedTime.equals("06 PM") || formattedTime.equals("07 PM")){
            System.out.println("Time: "+ formattedTime);
            HashSet<String> set = new HashSet<>();

            for(int i=0; i< messageQueue.length; i++){
                if(
                        messageQueue[i][0].equals("08 PM") ||
                                messageQueue[i][0].equals("09 PM") ||
                                messageQueueRH[i][0].equals("08 PM") ||
                                messageQueueRH[i][0].equals("09 PM") ||
                                messageQueueRP[i][0].equals("08 PM") ||
                                messageQueueRP[i][0].equals("09 PM") ||
                                messageQueueWS[i][0].equals("08 PM") ||
                                messageQueueWS[i][0].equals("09 PM") ||
                                messageQueueST[i][0].equals("08 PM") ||
                                messageQueueST[i][0].equals("09 PM")
                ){
                    set.add(messageQueue[i][1]);
                    set.add(messageQueueRP[i][1]);
                    set.add(messageQueueRH[i][1]);
                    set.add(messageQueueST[i][1]);
                    set.add(messageQueueWS[i][1]);
                }
            }

            sendNotificationToFrontend(month, set);
        }
        else{
            String notificationMessage = "Notification service temporary stopped until morning 4 AM\n Stay Tuned!";
            System.out.println(notificationMessage);
            firebaseMessagingService.sendNotificationByToken(notificationMessage);
        }


    }

    private void sendNotificationToFrontend(int month, HashSet<String> set) {
        for(String item: set){
            System.out.println(item);
            String notification = findNotification.findNotification(month, item);
            System.out.println(notification);
            firebaseMessagingService.sendNotificationByToken(notification);
            try{
                Thread.sleep(1000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

}
