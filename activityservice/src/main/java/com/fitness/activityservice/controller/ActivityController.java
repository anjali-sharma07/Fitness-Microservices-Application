package com.fitness.activityservice.controller;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody @Valid ActivityRequest request){
        ActivityResponse response = activityService.trackActivity(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivities(@RequestHeader ("X-User-Id") String userId){
        return new ResponseEntity<>(activityService.getUserActivities(userId), HttpStatus.OK);
    }

    @GetMapping("/{ActivityId}")
    public ResponseEntity<ActivityResponse> getActivityById(@PathVariable String ActivityId){
        return new ResponseEntity<>(activityService.getActivityById(ActivityId), HttpStatus.OK);
    }

}
