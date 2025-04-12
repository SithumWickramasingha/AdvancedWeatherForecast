package com.evertea.AdvancedWeatherApp.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Service
public class ApiResponse {

    public static String readApiResponses(HttpURLConnection apiConnection){
        try{
            // create a string builder to store the resulting JSON data
            StringBuilder resultJson = new StringBuilder();

            //create a scanner to read from the InputStream of the HttpURLConnection
            Scanner scanner = new Scanner(apiConnection.getInputStream());

            // loop through each line in the response and append it to the StringBuilder
            while(scanner.hasNext()) {
                //read and append the current line to the StringBuilder
                resultJson.append(scanner.nextLine());
            }

            return resultJson.toString(); // return the Json data as a string

        } catch(IOException e){
            e.printStackTrace();

        }

        return null;
    }

    public static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //set request method to get
            connection.setRequestMethod("GET");

            return connection;
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;

    }

}
