package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public List<Recommendation> getRecommendationByUserId(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    public Recommendation getRecommendationByActivityId(String activityId) {
        return recommendationRepository.findByActivityId(activityId)
        .orElseThrow(() -> new RuntimeException("Recommendation is not found for this activity"+ activityId));
    }
}
