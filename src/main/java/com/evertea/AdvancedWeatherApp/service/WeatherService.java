package com.evertea.AdvancedWeatherApp.service;


import com.evertea.AdvancedWeatherApp.AdvancedWeatherAppApplication;
import com.evertea.AdvancedWeatherApp.DTO.LocationAndTokenReceiver;
import com.evertea.AdvancedWeatherApp.DTO.WeatherData;
import com.evertea.AdvancedWeatherApp.repo.WeatherRepository;
import com.evertea.AdvancedWeatherApp.webSockets.WeatherDataWebSocketHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    //Declare city variable
    String location;
    double latitude = 0;
    double longitude = 0;

    String token;

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private WeatherNotificationService notificationService;

    @Autowired
    private WeatherDataWebSocketHandler webSocketHandler;

    @Autowired
    private AdvancedWeatherAppApplication application;

    @Autowired
    private DynamicTableService dynamicTableService;

    public void retrieveLocation(LocationAndTokenReceiver receiver){
        latitude = receiver.getLatitude();
        longitude = receiver.getLongitude();
        token = receiver.getFcmToken();

        System.out.println("lat: "+ latitude);
        System.out.println("lon: "+ longitude);
    }

    public String getLocationName(LocationAndTokenReceiver receiver){
        String APIKey = "AIzaSyDjb3uGFLqqh-fFtVbeP7cVnQ9ktpg7yNU";
        String url =    "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                +receiver.getLatitude()
                + ","
                +receiver.getLongitude()
                +"&key="
                +APIKey;

        Map response = application.restTemplate().getForObject(url, Map.class);
        if(response != null && response.containsKey("results")){
            List<Map> results = (List<Map>) response.get("results");
            if(!results.isEmpty()){
                location = ((Map)((java.util.List) response.get("results")).get(0)).get("formatted_address").toString();
                System.out.println("Reversing coordinates: "+ location);
                return location;
            }
        }
        return "Location not found";
    }

    int month = 1; // this is plantation age
//    int id =1;
//    private int getPlantationAge(){
//
//        PlantationData data = PlantationRepository.findBy(id)
//                .orElseThrow(() -> new RuntimeException("Plant not found"));
//
//    }

    @Scheduled(fixedRate = 10000, initialDelay = 2000)
    private void displayWeatherData(){





        System.out.println(setDateTime()); // call the method

        // prevent the program execute until get the API response
        if(latitude == 0 && longitude == 0){
            System.out.println("Latitude and Longitude is null, waiting for API response...");
            return;
        }

        if(token == null){
            System.out.println("Token is not fetched, waiting for fcm token...");
            return;
        }

        // create an instance of WeatherData
        WeatherData weatherData = new WeatherData();

        try{
            //API key
            String url =    "https://api.open-meteo.com/v1/forecast?latitude="+
                    latitude+
                    "&longitude="+
                    longitude+
                    "&hourly=temperature_2m,relative_humidity_2m,precipitation,rain,weather_code,wind_speed_10m,wind_direction_10m,soil_temperature_0cm&timezone=auto&forecast_days=1";


            // fetch API response
            HttpURLConnection apiConnection = ApiResponse.fetchApiResponse(url);

            //check response status
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return;
            }

            // read the response and convert store String type
            String jsonResponse = ApiResponse.readApiResponses(apiConnection);

            // parse the string into a JSON
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);

            JSONObject hourlyWeatherJson = (JSONObject) jsonObject.get("hourly");

            // checking purpose

            System.out.println("Hourly Weather: "+ hourlyWeatherJson.toString());

            JSONArray dateTimeArray = (JSONArray) hourlyWeatherJson.get("time");
            JSONArray temperatureArray = (JSONArray) hourlyWeatherJson.get("temperature_2m");
            JSONArray relativeHumidityArray = (JSONArray) hourlyWeatherJson.get("relative_humidity_2m");
            JSONArray precipitationArray = (JSONArray) hourlyWeatherJson.get("precipitation");
            JSONArray rainArray = (JSONArray) hourlyWeatherJson.get("rain");
            JSONArray weatherCodeArray = (JSONArray) hourlyWeatherJson.get("weather_code");
            JSONArray windSpeedArray = (JSONArray) hourlyWeatherJson.get("wind_speed_10m");
            JSONArray windDirectionArray = (JSONArray) hourlyWeatherJson.get("wind_direction_10m");
            JSONArray soilTempArray = (JSONArray) hourlyWeatherJson.get("soil_temperature_0cm");



            if(!weatherRepository.doesCityTableExist(location)){
                weatherRepository.createCityTableIfTableNotExist(location);
            }else{
                System.out.println(location+ "_plantation is already created");
            }

            // store hour in a array easy to read human
            String[] dateArray = {"12:00 AM","01:00 AM","02:00 AM","03:00 AM","04:00 AM","05:00 AM","06:00 AM","07:00 AM","08:00 AM","09:00 AM","10:00 AM","11:00 AM","12:00 PM","01:00 PM","02:00 PM","03:00 PM","04:00 PM","05:00 PM","06:00 PM","07:00 PM","08:00 PM","09:00 PM","10:00 PM","11:00 PM"};

            for(int i=6; i < 22; i++){

                String date = dateArray[i];
                weatherData.setDateTime(date);
                System.out.println("Date and Time: "+date);
                if(date.equals(setDateTime())){
                    System.out.println("----------");
                    System.out.println("   NOW    ");
                    System.out.println("----------");
                }

                double temperature = Math.round(((Number) temperatureArray.get(i)).doubleValue());
                weatherData.setTemp(temperature);
                System.out.println("Temperature: "+temperature);

                int relativeHumidity = Math.round(((Number) relativeHumidityArray.get(i)).intValue());
                weatherData.setRelativeHumidity(relativeHumidity);
                System.out.println("Relative Humidity: "+relativeHumidity);

                double precipitation = ((Number) precipitationArray.get(i)).doubleValue();
                weatherData.setPrecipitation(precipitation);
                System.out.println("Precipitation: "+precipitation);

                double rain = ((Number) rainArray.get(i)).doubleValue();
                weatherData.setRain(rain);
                System.out.println("Rain: "+rain);

                int weatherCode = ((Number) weatherCodeArray.get(i)).intValue();

                String feelsLike;

                switch (weatherCode){
                    case 0:
                        feelsLike = "Clear sky☀️";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 1:
                        feelsLike = "Mostly clear \uD83C\uDF24";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 2:
                        feelsLike = "Partly cloudy";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 3:
                        feelsLike = "Overcast";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 45:
                        feelsLike = "Fog";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 48:
                        feelsLike = "Depositing rime fog";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 51:
                        feelsLike = "Light drizzle";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 53:
                        feelsLike = "Moderate drizzle";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 55:
                        feelsLike = "Dense drizzle";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 56:
                        feelsLike = "Light freezing drizzle";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 57:
                        feelsLike = "Dense freezing drizzle";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 61:
                        feelsLike = "Slight rain";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                    case 63:
                        feelsLike = "Moderate rain";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 65:
                        feelsLike = "Heavy rain";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 66:
                        feelsLike = "Light freezing rain";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 67:
                        feelsLike = "Heavy freezing rain";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 80:
                        feelsLike = "Slight rain showers";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 81:
                        feelsLike = "Moderate rain showers";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 82:
                        feelsLike = "Violent rain showers";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                    case 95:
                        feelsLike = "Thunderstorm ";
                        weatherData.setWeatherCode(feelsLike);
                        System.out.println(feelsLike);
                        break;
                }

                double windSpeed = Math.round(((Number) windSpeedArray.get(i)).doubleValue());
                weatherData.setWindSpeed(windSpeed);
                System.out.println("Wind Speed: "+ windSpeed);

                double windDirection = ((Number) windDirectionArray.get(i)).doubleValue();

                String message;

                if(windDirection >= 338 || windDirection < 23){
                    message = "North (N)";
                    weatherData.setWindDirection(message);
                    System.out.println(message);
                }else if(windDirection < 68){
                    message = "North-East(NE)";
                    weatherData.setWindDirection(message);
                    System.out.println(message);
                }else if(windDirection < 113){
                    message = "East (E)";
                    weatherData.setWindDirection(message);
                    System.out.println(message);
                }else if(windDirection < 158){
                    message = "South-East (SE)";
                    weatherData.setWindDirection(message);
                    System.out.println(message);
                }else if(windDirection < 203){
                    message = "South (S)";
                    weatherData.setWindDirection(message);
                    System.out.println(message);
                }else if(windDirection < 248){
                    message = "South-West (SW)";
                    weatherData.setWindDirection(message);
                    System.out.println(message);
                }else if(windDirection < 293){
                    message = "West (W)";
                    weatherData.setWindDirection(message);
                    System.out.println(message);
                }else{
                    message = "North-West (NW)";
                    weatherData.setWindDirection(message);
                    System.out.println(message);
                }

                double soilTemperature = ((Number) soilTempArray.get(i)).doubleValue();
                weatherData.setSoilTemp(soilTemperature);
                System.out.println("Soil temperature: "+ soilTemperature);

                weatherRepository.insertWeatherData(location,
                        weatherData.getDateTime(),
                        weatherData.getTemp(),
                        weatherData.getRelativeHumidity(),
                        weatherData.getPrecipitation(),
                        weatherData.getWeatherCode(),
                        weatherData.getRain(),
                        weatherData.getWindSpeed(),
                        weatherData.getWindDirection(),
                        weatherData.getSoilTemp()
                );
            }

            //notificationService.getNotificationMessage(weatherData);
            String tableName =  location
                    .toLowerCase()
                    .replaceAll("[^a-z0-9_]", "_")
                    .replaceAll("_+","_")
                    .replaceAll("^_|_$","")
                    + "_plantation";
            System.out.println("Table name in service: "+tableName);
            dynamicTableService.getAllDataFromTable(tableName, month);

        }catch(IOException e){
            e.printStackTrace();
        }catch(ParseException e){
            e.printStackTrace();
        }
    }

    private String setDateTime(){

        LocalTime currentTime = LocalTime.now();

        return (currentTime.truncatedTo(ChronoUnit.HOURS)).toString();

    }


//    private void notifyWeatherDataWebSocket(String data, String type){
//        try{
//            String message = data
//                    .replace("\\", "\\\\")  // Escape backslashes
//                    .replace("\"", "\\\"")  // Escape double quotes
//                    .replace("\n", "\\n")   // Escape newlines
//                    .replace("\r", "\\r")   // Escape carriage returns
//                    .replace("\t", "\\t");  // Escape tabs
//
//            String dataJson = String.format(
//                    "{\"type\": \"%s\", \"message\": \"%s\"}",
//                    type,
//                    message
//            );
//
//            webSocketHandler.broadCast(dataJson);
//            System.out.println("Weather data send");
//
//        }catch(Exception e){
//            System.out.println("Error while notifying log websocket: "+ e.getMessage());
//        }
//
//    }
//


}
