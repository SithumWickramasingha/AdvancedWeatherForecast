package com.evertea.AdvancedWeatherApp.service;

import com.evertea.AdvancedWeatherApp.DTO.LocationAndTokenReceiver;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseMessagingService {

    String token;
    @Autowired
    private FirebaseMessaging firebaseMessaging;

    //private Firestore db;

    public void getTokenFromController(LocationAndTokenReceiver receiver){
        token = receiver.getFcmToken();
        System.out.println("Token from getTokenFromController: "+token);
    }

    public String sendNotificationByToken(String body){

        // To store notification when user in offline
        Map<String,String> data = new HashMap<>();

        String title = "☁️ System Alert!" + String.valueOf(new Date());


        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        try{
            firebaseMessaging.send(message);
            //saveNotificationToFirestore(title,body);
            System.out.println("Success sending push notification");
            return "Success sending notification";
        }catch (FirebaseMessagingException e){
            e.printStackTrace();
            return "Error sending notification";
        }


    }

//    public void saveNotificationToFirestore(String title, String body){
//        System.out.println("saveNotificationToFirestore called");
//        Map<String,Object> notification = new HashMap<>();
//
//        notification.put("title",title);
//        notification.put("body", body);
//        notification.put("timestamp",System.currentTimeMillis());
//        notification.put("status","pending");
//
//        db.collection("notifications").document(token)
//                .collection("userNotification")
//                .add(notification);
//
//        System.out.println("Saved notification to firestore");
//    }
}
