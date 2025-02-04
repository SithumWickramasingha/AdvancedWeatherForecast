package com.evertea.AdvancedWeatherApp.service;

import com.evertea.AdvancedWeatherApp.model.WeatherData;
import com.evertea.AdvancedWeatherApp.repo.WeatherRepo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Scanner;
import java.util.TimeZone;

@Service
public class WeatherService {

    public WeatherData getCity(WeatherData data){

        //Configuration configuration = new Configuration(data.getCity());
        WeatherData weatherData = new WeatherData();

        //System.out.println(configuration.getCity());

        // get location data
        JSONObject cityLocationData = (JSONObject) getLocationData(data.getCity());

        double latitude = (double) cityLocationData.get("latitude");
        double longitude = (double) cityLocationData.get("longitude");

        displayWeatherData(latitude, longitude, data.getCity());

        return weatherData;
    }

    private static JSONObject getLocationData(String city){

        WeatherData weatherData = new WeatherData();

        city = city.replaceAll(" ", "+");
        weatherData.setCity(city);

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

    @Autowired
    private WeatherRepo weatherRepo;


    private void displayWeatherData(double latitude, double longitude, String city){

        // create new object for store weather data
        WeatherData weatherData = new WeatherData();

        try{
            // Fetch the API response based on API link
            String url =    "https://api.open-meteo.com/v1/forecast?latitude="+
                            latitude+
                            "&longitude="+
                            longitude +
                            "&current=temperature_2m,relative_humidity_2m,is_day,precipitation,rain,wind_speed_10m,wind_direction_10m&daily=temperature_2m_min";


            // fetch API response
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
            //System.out.println(currentWeatherJson.toJSONString());

            // store the data into their corresponding data type

            if(currentWeatherJson == null){
                System.out.println("Error: current weather data not available");
                return;
            }

            // Extract and set weather data

            String timeZoneId = getTimeZoneFromLatLong(latitude, longitude);

            if(timeZoneId != null){
                ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of(timeZoneId));
                System.out.println("Current time in the location:" + currentTime);
                // set current date and time
                weatherData.setDateTime(currentTime);

            }else{
                System.out.println("Unable to determine time zone for the given coordinates");
            }

            double temp = ((Number) currentWeatherJson.get("temperature_2m")).doubleValue();
            weatherData.setTemp(temp);
            System.out.println("Current Temperature(2m) C: "+ temp);

            long relativeHumidity = ((Number) currentWeatherJson.get("relative_humidity_2m")).longValue();
            weatherData.setRelativeHumidity(relativeHumidity);
            System.out.println("Relative Humidity(2m): "+ relativeHumidity);

            long isDay = ((Number) currentWeatherJson.get("is_day")).longValue();
            weatherData.setDayNight(isDay == 1? "DAYTIME" : "NIGHTTIME");

            double precipitation = ((Number) currentWeatherJson.get("precipitation")).doubleValue();
            weatherData.setPrecipitation(precipitation);
            System.out.println("Precipitation" + precipitation);

            double rain = ((Number) currentWeatherJson.get("rain")).doubleValue();
            weatherData.setRain(rain);
            System.out.println("Rain: "+ rain);

            double wind_speed_10m = ((Number) currentWeatherJson.get("wind_speed_10m")).doubleValue();
            weatherData.setWindSpeed(wind_speed_10m);
            System.out.println("wind speed 10m: "+ wind_speed_10m);

            long wind_direction = ((Number) currentWeatherJson.get("wind_direction_10m")).longValue();
            //getWindDirection(wind_direction);

            String message;

            if(wind_direction >= 338 || wind_direction < 23){
                message = "North (N)";
                weatherData.setWindDirection(message);
                System.out.println(message);
            }else if(wind_direction >= 23 && wind_direction < 68){
                message = "North-East (NE)";
                weatherData.setWindDirection(message);
                System.out.println(message);
            }else if(wind_direction >= 68 && wind_direction < 113){
                message = "East (E)";
                weatherData.setWindDirection(message);
                System.out.println(message);
            }else if(wind_direction >= 113 && wind_direction < 158){
                message = "South- East (SE) ";
                weatherData.setWindDirection(message);
                System.out.println(message);
            }else if(wind_direction >= 158 && wind_direction < 203){
                message = "south (S)";
                weatherData.setWindDirection(message);
                System.out.println(message);
            }else if(wind_direction >= 203 && wind_direction < 248){
                message = "South-West (SW)";
                weatherData.setWindDirection(message);
                System.out.println(message);
            }else if(wind_direction >= 248 && wind_direction < 293){
                message = "West (W)";
                weatherData.setWindDirection(message);
                System.out.println(message);
            }else {
                message = "North-West (NW)";
                weatherData.setWindDirection(message);
                System.out.println(message);
            }


            System.out.println("API JSON Responses: "+ jsonResponse);
            System.out.println("city from weather data: "+ weatherData.getWindDirection());

            if(!weatherRepo.doesCityTableExist(weatherData.getDayNight())){
                weatherRepo.createCityTableIfNotExist(city);
                weatherRepo.insertWeatherData(  city,
                                                weatherData.getDateTime(),
                                                weatherData.getTemp(),
                                                weatherData.getRelativeHumidity(),
                                                weatherData.getDayNight(),
                                                weatherData.getPrecipitation(),
                                                weatherData.getRain(),
                                                weatherData.getWindSpeed(),
                                                weatherData.getWindDirection());
            }else{
                System.out.println("this table already created");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public WeatherData getAllData(){
        System.out.println("hello");
        return new WeatherData();
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
