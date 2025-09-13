package com.fitness.aiservice.service;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GroqService {

     @Autowired
     private WebClient webClient;


     @Value("${groq.api.uri}")
     private String GroqUri;

     @Value("${groq.api.key}")
     private String GroqKey;


     private GroqService(WebClient.Builder webClientBuilder){
          this.webClient = webClientBuilder.build();
     }
     public String getAnswer(String prompt){

          // craft request
          Map<String, Object> message = Map.of(
                  "role", "user",
                  "content", prompt   // your prompt for fitness JSON
          );

          Map<String, Object> requestBody = Map.of(
                  "model", "openai/gpt-oss-20b",
                  "messages", List.of(message)
          );

          String response = webClient.post()
                  .uri(GroqUri)  // e.g., "https://api.groq.com/openai/v1/chat/completions"
                  .header("Authorization", "Bearer " + GroqKey)
                  .header("Content-Type", "application/json")
                  .bodyValue(requestBody)
                  .retrieve()
                  .bodyToMono(String.class)
                  .block();

          return response;

     }

     }

