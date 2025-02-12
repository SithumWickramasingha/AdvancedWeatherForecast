package com.evertea.AdvancedWeatherApp.service;

import com.evertea.AdvancedWeatherApp.exceptions.NullPointException;
import com.evertea.AdvancedWeatherApp.model.WeatherData;
import com.evertea.AdvancedWeatherApp.repo.WeatherRepo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import javax.management.Notification;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

@Service
public class WeatherService {

    double latitude = 0;
    double longitude = 0;
    String city;

    // create reference for weather repository layer
    @Autowired
    private WeatherRepo weatherRepo;

    // create reference for weather notification service layer
    @Autowired
    private WeatherNotificationService weatherNotificationService;

    public WeatherData getCity(WeatherData data){
        city = data.getCity();

        //Configuration configuration = new Configuration(data.getCity());
        WeatherData weatherData = new WeatherData();

        //System.out.println(configuration.getCity());

        // get location data
        JSONObject cityLocationData = (JSONObject) getLocationData(city);

        latitude = (double) cityLocationData.get("latitude");
        longitude = (double) cityLocationData.get("longitude");

        //displayWeatherData(latitude, longitude, data.getCity());


        return weatherData;
    }

    private static JSONObject getLocationData(String city){

        WeatherData weatherData = new WeatherData();

        city = city.replaceAll(" ", "+");
        weatherData.setCity(city);

        // checking purpose
        System.out.println("location method called");
        System.out.println("------------------------------------------------------------------");
        System.out.println("City name from get location method: "+ city);

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

            System.out.println("API response: "+ jsonResponse.toString());

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


    @Scheduled(fixedRate = 10000, initialDelay = 6000)
    private void displayWeatherData() throws NullPointException {

        if(city == null || city.isBlank()){
            System.out.println("City is null. waiting for API response .........");
            return;
        }


        System.out.println("city: "+ city);

        // create new object for store weather data
        WeatherData weatherData = new WeatherData();

        try{
             //Fetch the API response based on API link
            String url =    "https://api.open-meteo.com/v1/forecast?latitude="+
                            latitude+
                            "&longitude="+
                            longitude+
                            "&current=temperature_2m,cloud_cover&daily=temperature_2m_max,temperature_2m_min,daylight_duration,sunshine_duration,uv_index_max,precipitation_sum,rain_sum,wind_speed_10m_max,wind_direction_10m_dominant&timezone=auto";

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
            JSONObject dailyWeatherJson = (JSONObject) jsonObject.get("daily");

            System.out.println("daily weather : "+dailyWeatherJson.toJSONString());

            // store the data into their corresponding data type

            if(currentWeatherJson == null){
                System.out.println("Error: current weather data not available");
                return;
            }




            JSONArray timeArray = (JSONArray) dailyWeatherJson.get("time");

            //Extract arrays
            JSONArray temperatureMaxArray = (JSONArray) dailyWeatherJson.get("temperature_2m_max");
            JSONArray temperatureMinArray = (JSONArray) dailyWeatherJson.get("temperature_2m_min");
            JSONArray dayLightArray = (JSONArray) dailyWeatherJson.get("daylight_duration");
            JSONArray sunShineArray = (JSONArray) dailyWeatherJson.get("sunshine_duration");
            JSONArray uvIndexArray = (JSONArray) dailyWeatherJson.get("uv_index_max");
            JSONArray precipitationSumArray = (JSONArray) dailyWeatherJson.get("precipitation_sum");
            JSONArray rainSumArray = (JSONArray) dailyWeatherJson.get("rain_sum");
            JSONArray windSpeedArray = (JSONArray) dailyWeatherJson.get("wind_speed_10m_max");
            JSONArray windDirectionArray = (JSONArray) dailyWeatherJson.get("wind_direction_10m_dominant");

            if(rainSumArray == null){
                System.out.println("no rain forecast");

            }

            // generate the weather table for each city
            // check the table was created or not
            if(!weatherRepo.doesCityTableExist(city)){
                weatherRepo.createCityTableIfNotExist(city);

            }else{
                System.out.println(city +"_weather table is already created");
            }

            // Extract and set weather data ------------------------------------------------





            for (int i=0; i < 1; i++){
                System.out.println("-------------------------------------------");

                String date = (String) timeArray.get(i);
                weatherData.setDateTime(date);
                System.out.println(date);


                int cloudCover = ((Number) currentWeatherJson.get("cloud_cover")).intValue();
                String coverage;
                if(cloudCover >= 0 && cloudCover <= 25){
                    coverage = "Mostly Sunny";
                    weatherData.setCloudCover(coverage);
                    System.out.println("cloud cover: "+ coverage);
                }else if(cloudCover >= 26 && cloudCover <= 50){
                    coverage = "Partly Cloudy";
                    weatherData.setCloudCover(coverage);
                    System.out.println("cloud cover: "+ coverage);
                }else if(cloudCover >= 51 && cloudCover <= 75){
                    coverage = "Mostly Cloudy";
                    weatherData.setCloudCover(coverage);
                    System.out.println("cloud cover: "+ coverage);
                }else{
                    coverage = "Overcast";
                    weatherData.setCloudCover(coverage);
                    System.out.println("cloud cover: "+ coverage);
                }

                double currentTemp = Math.round(((Number) currentWeatherJson.get("temperature_2m")).doubleValue());
                weatherData.setCurrentTemp(currentTemp);
                System.out.println("Current temperature: "+ currentTemp);

                //
                double tempMax = Math.round(((Number) temperatureMaxArray.get(i)).doubleValue());
                weatherData.setTempMax(tempMax);
                System.out.println("Maximum Temperature(2m) C: "+ tempMax);

                double tempMin = Math.round(((Number) temperatureMinArray.get(i)).doubleValue());
                weatherData.setTempMin(tempMin);
                System.out.println("Minimum Temperature(2m) c: "+ tempMin);

                long dayLight = ((Number) dayLightArray.get(i)).longValue();
                long dayLightHour = dayLight/ 3600;
                weatherData.setDayLight(dayLightHour);
                System.out.println("Day light (hourly): "+ dayLightHour);

                long sunShine = ((Number) sunShineArray.get(i)).longValue();
                long sunShineHour = sunShine / 3600;
                weatherData.setSunShine(sunShineHour);
                System.out.println("Sun shine (h): "+ sunShineHour);

                double uvIndexMax = Math.round(((Number) uvIndexArray.get(i)).doubleValue());
                weatherData.setUvIndexMax(uvIndexMax);
                System.out.println("UV index max: "+ uvIndexMax);

                double precipitationSum = Math.round(((Number) precipitationSumArray.get(i)).doubleValue());
                weatherData.setPrecipitationSum(precipitationSum);
                System.out.println("Precipitation Sum: " + precipitationSum);

                double rainSum = Math.round(((Number) rainSumArray.get(i)).doubleValue());
                weatherData.setRainSum(rainSum);
                System.out.println("Rain sum: "+ rainSum);

                double wind_speed_max_10m = Math.round(((Number) windSpeedArray.get(i)).doubleValue());
                weatherData.setWindSpeedMax(wind_speed_max_10m);
                System.out.println("wind speed 10m: "+ wind_speed_max_10m);


                long wind_direction = Math.round(((Number) windDirectionArray.get(i)).longValue());

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

                weatherRepo.insertWeatherData(city,
                        weatherData.getDateTime(),
                        weatherData.getCloudCover(),
                        weatherData.getCurrentTemp(),
                        weatherData.getTempMax(),
                        weatherData.getTempMin(),
                        weatherData.getDayLight(),
                        weatherData.getSunShine(),
                        weatherData.getUvIndexMax(),
                        weatherData.getPrecipitationSum(),
                        weatherData.getRainSum(),
                        weatherData.getWindSpeedMax(),
                        weatherData.getWindDirection());

            }

            weatherNotificationService.getNotificationMessage(weatherData);


        }catch(Exception e){
            e.printStackTrace();
        }
    }



    // retrieve the all data to frontend
    public List<WeatherData> getWeatherData(String city){
        return weatherRepo.getAllWeatherData(city);
    }


//    @Scheduled(fixedRate = 10000, initialDelay = 5000)
//    public void getMessage(){
//
//    }



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
