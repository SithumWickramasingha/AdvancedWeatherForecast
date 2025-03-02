package com.evertea.AdvancedWeatherApp.repo;

import com.evertea.AdvancedWeatherApp.DTO.WeatherNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface NotificationRepo extends JpaRepository<WeatherNotification, Long> {

    WeatherNotification findById(int id);
}
