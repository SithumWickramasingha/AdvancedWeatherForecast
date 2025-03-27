package com.evertea.AdvancedWeatherApp.repo;

import com.evertea.AdvancedWeatherApp.DTO.PlantationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface PlantationRepository extends JpaRepository<PlantationData, Integer> {

    PlantationData findBy(int id);
}
