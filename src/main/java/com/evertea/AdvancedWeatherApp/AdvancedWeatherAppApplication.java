package com.evertea.AdvancedWeatherApp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling

public class AdvancedWeatherAppApplication {

	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException{
		System.out.println("fire base messaging called");
		GoogleCredentials googleCredentials = GoogleCredentials
				.fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());

		FirebaseOptions firebaseOptions = FirebaseOptions.builder()
				.setCredentials(googleCredentials).build();
		FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "my-app");

		return FirebaseMessaging.getInstance(app);
	}

	@Bean
	public RestTemplate restTemplate(){
		System.out.println("rest template called");
		return new RestTemplate();
	}



	public static void main(String[] args) {
		SpringApplication.run(AdvancedWeatherAppApplication.class, args);
		System.out.println();
		System.out.println("-------------Advanced Weather Forecast Application------------");
		System.out.println();
	}

}
