package com.fitness.activityservice.dto;

import com.fitness.activityservice.model.ActivityType;
import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityRequest {


    @NotBlank(message = "User ID cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{6,50}$", message = "Invalid User ID format")
    private String userId;

    @NotNull(message = "Activity type is required")
    private ActivityType type;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 1440, message = "Duration cannot exceed 1440 minutes (24 hours)")
    private Integer duration;

    @NotNull(message = "Calories burnt is required")
    @Min(value = 0, message = "Calories cannot be negative")
    @Max(value = 10000, message = "Calories burnt seems unrealistic")
    private Integer caloriesBurnt;


    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    private Map<String, Object> additionalMatrices;
}
