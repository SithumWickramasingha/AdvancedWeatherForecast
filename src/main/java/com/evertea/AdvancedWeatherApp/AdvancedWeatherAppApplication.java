package com.evertea.AdvancedWeatherApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdvancedWeatherAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdvancedWeatherAppApplication.class, args);
		System.out.println();
		System.out.println("-------------Advanced Weather Forecast Application------------");
		System.out.println();
	}

}
