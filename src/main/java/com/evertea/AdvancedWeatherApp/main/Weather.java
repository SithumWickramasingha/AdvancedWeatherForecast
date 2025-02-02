package com.evertea.AdvancedWeatherApp.main;

import com.evertea.AdvancedWeatherApp.config.Configuration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

@Component
public class Weather {

    public Configuration getCity(Configuration config){

        Configuration configuration = new Configuration(config.getCity());

        //System.out.println(configuration.getCity());

        // get location data
        JSONObject cityLocationData = (JSONObject) getLocationData(config.getCity());

        double latitude = (double) cityLocationData.get("latitude");
        double longitude = (double) cityLocationData.get("longitude");

        displayWeatherData(latitude, longitude);

        return configuration;
    }

    private static JSONObject getLocationData(String city){

        city = city.replaceAll(" ", "+");

        // checking purpose
        System.out.println("location method called");
        System.out.println("City name:"+ city);

        String urlString =  "https://geocoding-api.open-meteo.com/v1/search?name=" +
                            city +
                            "&count=1&language=en&format=json";

        try{
            // Fetch the API response based on API link
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            // check for response status
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: could not connect API");
                return null;
            }

            // Read the response and Convert store string type
            String jsonResponse = readApiResponses(apiConnection);

            // parse the string into a json object
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObject = (JSONObject) parser.parse(jsonResponse);

            // Retrieve location data
            JSONArray locationData = (JSONArray) resultJsonObject.get("results");
            return (JSONObject) locationData.get(0);
        }catch(Exception e){
            e.printStackTrace();
        }


        return null;
    }

    private static void displayWeatherData(double latitude, double longitude){
        try{
            // Fetch the API response based on API link
            String url =    "https://api.open-meteo.com/v1/forecast?latitude="+
                            latitude+
                            "&longitude="+
                            longitude +
                            "&current=temperature_2m,relative_humidity_2m,is_day,precipitation,rain,wind_speed_10m,wind_direction_10m&daily=temperature_2m_min";


            HttpURLConnection apiConnection = fetchApiResponse(url);

            // check response status
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return;
            }

            // read the response and convert store String type
            String jsonResponse = readApiResponses(apiConnection);

            // parse the string into a Json
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current");
            System.out.println(currentWeatherJson.toJSONString());

            // store the data into their corresponding data type
//            String time = (String) currentWeatherJson.get("time");
//            System.out.println("Current time: "+ time);

            String timeZoneId = getTimeZoneFromLatLong(latitude, longitude);

            if(timeZoneId != null){
                ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of(timeZoneId));
                System.out.println("Current time in the location:" + currentTime);

            }else{
                System.out.println("Unable to determine time zone for the given coordinates");
            }

            double temp = (double) currentWeatherJson.get("temperature_2m");
            System.out.println("Current Temperature(2m) C: "+ temp);

            long relativeHumidity = (long) currentWeatherJson.get("relative_humidity_2m");
            System.out.println("Relative Humidity(2m): "+ relativeHumidity);

            long isDay = (long) currentWeatherJson.get("is_day");
            System.out.println("Is day or night: " + isDay);

            double precipitation = (double) currentWeatherJson.get("precipitation");
            System.out.println("Precipitation" + precipitation);

            double rain = (double) currentWeatherJson.get("rain");
            System.out.println("Rain: "+ rain);

            double wind_speed_10m = (double) currentWeatherJson.get("wind_speed_10m");
            System.out.println("wind speed 10m: "+ wind_speed_10m);

            long wind_direction = (long) currentWeatherJson.get("wind_direction_10m");
            System.out.println("wind_direction_10m: " + wind_direction);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static String getTimeZoneFromLatLong(double lat, double lon){

        TimeZone time = TimeZone.getTimeZone("GMT");
        return time.getID();
    }
    private static String readApiResponses(HttpURLConnection apiConnection){
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

    private static HttpURLConnection fetchApiResponse(String urlString){
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
