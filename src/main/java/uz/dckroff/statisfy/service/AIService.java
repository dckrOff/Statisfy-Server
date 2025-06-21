package uz.dckroff.statisfy.service;

import uz.dckroff.statisfy.dto.ai.AIResponse;
import uz.dckroff.statisfy.dto.fact.FactResponse;
import uz.dckroff.statisfy.model.User;

public interface AIService {
    
    /**
     * Generates a fact about the given topic in the specified language
     * 
     * @param topic the topic to generate a fact about
     * @param language the language to generate the fact in (optional)
     * @return AIResponse containing the generated fact
     */
    AIResponse generateFact(String topic, String language);
    
    /**
     * Analyzes the relevance of a news article for a user with given interests
     * 
     * @param newsId the ID of the news article to analyze
     * @param userInterests the user's interests (optional)
     * @return AIResponse containing the analysis result
     */
    AIResponse analyzeNewsRelevance(Long newsId, String userInterests);
    
    /**
     * Generates a personalized daily fact for a user based on their preferences
     * 
     * @param user the user to generate a fact for
     * @return FactResponse containing the generated fact
     */
    FactResponse generateDailyFact(User user);
} 