package com.evertea.AdvancedWeatherApp.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WeatherRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean doesCityTableExist(String location){
        String tableName =  location
                .toLowerCase()
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+","_")
                .replaceAll("^_|_$","")
                + "_plantation";

        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);

        return count > 0;
    }

    public void createCityTableIfTableNotExist(String location){
        String tableName =  location
                .toLowerCase()
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+","_")
                .replaceAll("^_|_$","")
                + "_plantation";

        String createTableSql =    "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "Plantation_name VARCHAR(100) NOT NULL, "
                + "Date_Time VARCHAR(50) NOT NULL UNIQUE, "
                + "Temperature DECIMAL(5,2), "
                + "Relative_humidity DECIMAL(5,2), "
                + "Precipitation DECIMAL(5,2), "
                + "Rain DECIMAL(5,2), "
                + "Weather_code VARCHAR(50), "
                + "wind_speed_max DECIMAL(5,2),"
                + "wind_direction VARCHAR(50), "
                + "Soil_Temperature DECIMAL(5,2) "
                + ")";

        System.out.println(tableName + " created");
        jdbcTemplate.execute(createTableSql);
    }

    public void insertWeatherData(String location, String dateTime, double temp, int relativeHumidity, double precipitation,String weatherCode, double rain,double windSpeedMax, String windDirection, double soilTemp){
        String tableName =  location
                .toLowerCase()
                .replaceAll("[^a-z0-9_]", "_")
                .replaceAll("_+","_")
                .replaceAll("^_|_$","")
                + "_plantation";

        String sql =    "INSERT INTO "+ tableName + " (Plantation_name, Date_Time,Temperature, Relative_Humidity, Precipitation, Rain, Weather_code, wind_speed_max, wind_direction, Soil_Temperature) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE "+
                "Temperature = VALUES(Temperature), "+
                "Relative_Humidity = VALUES(Relative_Humidity), "+
                "Precipitation = VALUES(Precipitation), "+
                "Rain = VALUES(Rain), "+
                "Weather_code = VALUES(Weather_code), "+
                "wind_speed_max = VALUES(wind_speed_max), "+
                "wind_direction = VALUES(wind_direction), "+
                "Soil_Temperature = VALUES(Soil_Temperature)";

        jdbcTemplate.update(sql, location, dateTime,temp, relativeHumidity, precipitation, rain, weatherCode, windSpeedMax, windDirection, soilTemp);

    }
}
