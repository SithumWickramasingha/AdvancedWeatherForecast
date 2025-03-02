package com.evertea.AdvancedWeatherApp.repo;

import com.evertea.AdvancedWeatherApp.DTO.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@EnableJpaRepositories
public class WeatherRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean doesCityTableExist(String city)  {

        String tableName = city.replaceAll("\\s+", "_").toLowerCase() + "_weather";

        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);

        return count > 0;
    }

    public void createCityTableIfNotExist(String city){
        String tableName = city.replaceAll("\\s+", "_").toLowerCase()+"_weather";
        String createTableSql =    "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "city VARCHAR(50) NOT NULL, "
                + "date_time VARCHAR(50) NOT NULL, "
                + "cloud_cover VARCHAR(50), "
                + "current_temp DECIMAL(5,2), "
                + "maximum_temp DECIMAL(5,2), "
                + "minimum_temp DECIMAL(5,2), "
                + "day_light DECIMAL(5,2), "
                + "sun_shine DECIMAL(5,2), "
                + "uv_index_max DECIMAL(5,2),"
                + "precipitation_sum DECIMAL(5,2), "
                + "rain_sum DECIMAL(5,2), "
                + "wind_speed_max DECIMAL(5,2),"
                + "wind_direction VARCHAR(50)"
                + ")";


        System.out.println(tableName+ " Table created");
        jdbcTemplate.execute(createTableSql);

    }

    public void insertWeatherData(String city, String dateTime,String cloud_cover,double currentTemp, double tempMax,double tempMin,long dayLight,long sunShine, double uvIndexSum, double precipitationSum, double rainSum, double windSpeedMax, String windDirection){
        String tableName = city.replaceAll("\\s","_").toLowerCase()+"_weather";
        String sql = "INSERT INTO "+ tableName + " (city, date_time,cloud_cover,current_temp, maximum_temp, minimum_temp, day_light, sun_shine, uv_index_max, precipitation_sum, rain_sum, wind_speed_max, wind_direction) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

        jdbcTemplate.update(sql, city, dateTime, cloud_cover,currentTemp, tempMax,tempMin, dayLight,sunShine, uvIndexSum, precipitationSum, rainSum, windSpeedMax, windDirection);

    }

    public List<WeatherData> getAllWeatherData(String city){

        if(city == null || city.isBlank()){
            System.out.println("City cannot be null");
        }

        String query = "SELECT * FROM " + city.replace(" " ,"_")+ "_weather";
        return jdbcTemplate.query(query, (rs, rowNum) ->
                new WeatherData(
                        rs.getInt("id"),
                        rs.getString("city"),
                        rs.getString("date_time"),
                        rs.getString("cloud_cover"),
                        rs.getDouble("current_temp"),
                        rs.getDouble("maximum_temp"),
                        rs.getDouble("minimum_temp"),
                        rs.getLong("day_light"),
                        rs.getLong("sun_shine"),
                        rs.getDouble("uv_index_max"),
                        rs.getInt("precipitation_sum"),
                        rs.getDouble("rain_sum"),
                        rs.getDouble("wind_speed_max"),
                        rs.getString("wind_direction")
                )
        );
    }



}
