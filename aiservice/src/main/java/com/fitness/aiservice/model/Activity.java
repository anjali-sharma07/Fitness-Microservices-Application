package com.fitness.aiservice.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Activity {

    private String id;
    private String userId;
    private String type;
    private Integer duration;
    private Integer caloriesBurnt;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMatrices;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



}
