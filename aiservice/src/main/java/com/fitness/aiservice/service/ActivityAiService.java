package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j

public class ActivityAiService {

    @Autowired
    private GroqService groqService;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPrompt(activity);
        String aiResponse = groqService.getAnswer(prompt);
        log.info("RESPONSE FROM AI : {}", aiResponse);
        processAiResponse(activity, aiResponse);
        return   processAiResponse(activity, aiResponse);
    }

    public Recommendation processAiResponse(Activity activity, String aiResponse){
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);

            // Navigate to choices[0].message.content
            JsonNode contentNode = rootNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content");

            String jsonContent = contentNode.asText()
                    .replaceAll("```json\\n","")  // remove code block formatting if present
                    .replaceAll("\\n```" , "")
                    .trim();


           // log.info("PARSED RESPONSE FROM AI : {}", jsonContent);

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.get("analysis");

            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall : ");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace : ");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate : ");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurnt", "Calories : ");

            List<String> improvements = extractImprovements(analysisJson.path("improvements"));

            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));

            List<String> safety = extractSafetyGuidlines(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch(Exception e){
            e.printStackTrace();
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Consider consulting a fitness professional"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidlines(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(item ->  safety.add(item.asText()));

            return safety.isEmpty()?
                    Collections.singletonList("Follow general safety guidlines"):
                    safety;
        }

        return safety.isEmpty()?
                Collections.singletonList("Follow general safety guidlines"):
                safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if(suggestionsNode.isArray()){
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s" , workout, description));
            });
            return suggestions.isEmpty()?
                    Collections.singletonList("No specific suggestions provided"):
                    suggestions;
        }
        return suggestions.isEmpty()?
                Collections.singletonList("No specific suggestions provided"):
                suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s" , area, detail));
            });
            return improvements.isEmpty()?
                    Collections.singletonList("No specific improvements provided"):
                    improvements;
        }
        return improvements.isEmpty()?
                Collections.singletonList("No specific improvements provided"):
                improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                    .append(" ")
                    .append(analysisNode.path(key).asText())
                    .append("   ");

        }
    }


    private String createPrompt(Activity activity) {
        return String.format("""
        You are a fitness AI assistant. Analyse the given activity and return ONLY valid JSON in the exact structure below. 
        Do not add explanations, markdown, or text outside of JSON.

        Activity details:
        - Type = %s
        - Duration = %d minutes
        - CaloriesBurnt = %d
        - AdditionalMetrics = %s

        JSON FORMAT:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurnt": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Next workout suggestion",
              "description": "Workout description"
            }
          ],
          "safety": [
            "Safety tip 1",
            "Safety tip 2"
          ]
        }
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurnt(),
                activity.getAdditionalMatrices()
        );
    }

}