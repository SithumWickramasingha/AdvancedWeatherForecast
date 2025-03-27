package com.evertea.AdvancedWeatherApp.service;

import com.evertea.AdvancedWeatherApp.DTO.LocationAndTokenReceiver;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FirebaseMessagingService {

    String token;

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    public void getTokenFromController(LocationAndTokenReceiver notification){
        token = notification.getRecipientToken();
        System.out.println("controller : "+token);
    }

    public String sendNotificationByToken(String message){
        System.out.println("send notification by token called");

        String title = "☁️ System Alert!" + String.valueOf(new Date());
        String body = message;


//        Map<String, String> data = new HashMap<>();
//        data.put("title", title);
//        data.put("body", body);
//        data.put("Time", String.valueOf(new Date()));




        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message1 = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();



        try{
            firebaseMessaging.send(message1);
            return "Success sending notification";
        }catch(FirebaseMessagingException e){
            e.printStackTrace();
            return "Error sending notification";
        }



    }
}
