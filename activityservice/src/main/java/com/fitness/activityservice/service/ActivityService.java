package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidateService userValidateService;
    private final RabbitTemplate rabbitTemplate;


    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingkey;

    public ActivityResponse trackActivity(ActivityRequest request) {
        boolean isValidUser = userValidateService.validUser(request.getUserId());
        if(!isValidUser)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found: " + request.getUserId());
        Activity activity = new Activity();
        activity.setUserId(request.getUserId());
        activity.setType(request.getType());
        activity.setDuration(request.getDuration());
        activity.setCaloriesBurnt(request.getCaloriesBurnt());
        activity.setStartTime(request.getStartTime());
        activity.setAdditionalMatrices(request.getAdditionalMatrices());

        Activity savedActivity = activityRepository.save(activity);      // store in db.

        try{
             rabbitTemplate.convertAndSend(exchange, routingkey, savedActivity );
        }catch(Exception e){
            log.error("Failed to publish activity to RabbitMQ" , e);
        }
         return maptoresponse(savedActivity);
    }

    public ActivityResponse maptoresponse(Activity savedActivity){

        ActivityResponse response = new ActivityResponse();
        response.setId(savedActivity.getId());
        response.setUserId(savedActivity.getUserId());
        response.setType(savedActivity.getType());
        response.setDuration(savedActivity.getDuration());
        response.setCaloriesBurnt(savedActivity.getCaloriesBurnt());
        response.setStartTime(savedActivity.getStartTime());
        response.setAdditionalMatrices(savedActivity.getAdditionalMatrices());
        response.setCreatedAt(savedActivity.getCreatedAt());
        response.setUpdatedAt(savedActivity.getUpdatedAt());

        return response;
    }

    public List<ActivityResponse> getUserActivities(String userId) {
        boolean isValidUser = userValidateService.validUser(userId);
        if(!isValidUser)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found: " + userId);
        List<Activity> useractivities = activityRepository.findByUserId(userId);
        List<ActivityResponse> response = useractivities.stream()
                .map(this:: maptoresponse)
                .collect(Collectors.toList());
            return response;
                }

    public ActivityResponse getActivityById(String activityId) {
     return activityRepository.findById(activityId)
                .map(this::maptoresponse)
                .orElseThrow(() -> new RuntimeException("Activity not found with this Id "+ activityId));

    }
}
