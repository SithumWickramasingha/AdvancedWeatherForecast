package com.evertea.AdvancedWeatherApp.repo;

import com.evertea.AdvancedWeatherApp.model.WeatherData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@EnableJpaRepositories
public class WeatherRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean doesCityTableExist(String city){
        String tableName = city.replaceAll("\\s+", "_").toLowerCase() + "_weather";

        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);

        return count != null && count > 0;
    }

    public void createCityTableIfNotExist(String city){
        String tableName = city.replaceAll("\\s+", "_").toLowerCase()+"_weather";
        String sql =    "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                        + "id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "city VARCHAR(50) NOT NULL, "
                        + "date_time DATETIME NOT NULL, "
                        + "temp DECIMAL(5,2), "
                        + "relative_humidity INT, "
                        + "day_night VARCHAR(10), "
                        + "precipitation INT, "
                        + "rain DECIMAL(5,2), "
                        + "wind_speed DECIMAL(5,2)"
                        + "wind_direction DECIMAL(5,2)";

        System.out.println(tableName+ "Table created");
        jdbcTemplate.execute(sql);
    }

    //public void insertWeatherData(String city, LocalDateTime dateTime, double temp, ){}
}
