package com.evertea.AdvancedWeatherApp.webSockets;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WeatherDataWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        System.out.println("--------------------------------------");
        System.out.println("Connection Established");
        System.out.println("--------------------------------------");
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status){
        System.out.println("--------------------------------------");
        System.out.println("Disconnected");
        System.out.println("--------------------------------------");
        sessions.remove(session);
    }

    public synchronized void broadCast(String message){
        for(WebSocketSession session: sessions){
            if(session.isOpen()){
                try{
                    session.sendMessage(new TextMessage(message));
                }catch(IOException e){
                    System.out.println(e.getMessage());
                }
            }
        }
    }

}
