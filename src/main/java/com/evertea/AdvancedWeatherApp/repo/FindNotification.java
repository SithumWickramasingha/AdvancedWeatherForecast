package com.evertea.AdvancedWeatherApp.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.stereotype.Repository;

@Repository
public class FindNotification {

    @Autowired
    private EntityManager entityManager;

    public String findNotification(int month, String id){

        String tableName = "weather_conditions_month"+ month;

        String sql = "SELECT notification_message FROM " + tableName + " WHERE id = :id";

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("id", id);

        return (String) query.getSingleResult();
    }
}
