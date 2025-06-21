package uz.dckroff.statisfy.service.impl;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uz.dckroff.statisfy.dto.ai.AIResponse;
import uz.dckroff.statisfy.dto.category.CategoryResponse;
import uz.dckroff.statisfy.dto.fact.FactResponse;
import uz.dckroff.statisfy.exception.ResourceNotFoundException;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.model.Fact;
import uz.dckroff.statisfy.model.News;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.repository.CategoryRepository;
import uz.dckroff.statisfy.repository.FactRepository;
import uz.dckroff.statisfy.repository.NewsRepository;
import uz.dckroff.statisfy.repository.UserPreferenceRepository;
import uz.dckroff.statisfy.service.AIService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {

    private final OpenAiService openAiService;
    private final NewsRepository newsRepository;
    private final FactRepository factRepository;
    private final CategoryRepository categoryRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    
    @Value("${openai.api.model}")
    private String model;
    
    private static final Random random = new Random();

    @Override
    public AIResponse generateFact(String topic, String language) {
        try {
            log.info("Generating fact about '{}' in language '{}'", topic, language);
            
            String prompt = buildFactGenerationPrompt(topic, language);
            String response = callOpenAI(prompt);
            
            return AIResponse.builder()
                    .content(response)
                    .success(true)
                    .message("Fact generated successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error generating fact", e);
            return AIResponse.builder()
                    .success(false)
                    .message("Error generating fact: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public AIResponse analyzeNewsRelevance(Long newsId, String userInterests) {
        try {
            log.info("Analyzing relevance of news with ID {} for interests '{}'", newsId, userInterests);
            
            News news = newsRepository.findById(newsId)
                    .orElseThrow(() -> new ResourceNotFoundException("News not found with id: " + newsId));
            
            String prompt = buildNewsRelevancePrompt(news, userInterests);
            String response = callOpenAI(prompt);
            
            // Update news relevance based on AI response
            boolean isRelevant = response.toLowerCase().contains("relevant") || 
                                response.toLowerCase().contains("yes");
            
            news.setRelevant(isRelevant);
            newsRepository.save(news);
            
            return AIResponse.builder()
                    .content(response)
                    .success(true)
                    .message("News analyzed successfully")
                    .build();
        } catch (Exception e) {
            log.error("Error analyzing news relevance", e);
            return AIResponse.builder()
                    .success(false)
                    .message("Error analyzing news: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public FactResponse generateDailyFact(User user) {
        try {
            log.info("Generating daily fact for user {}", user.getUsername());
            
            // Get user preferences if available
            var userPreferences = userPreferenceRepository.findByUser(user);
            
            // Select a category based on user preferences or random
            Category category;
            if (userPreferences.isPresent() && !userPreferences.get().getPreferredCategories().isEmpty()) {
                List<Category> preferredCategories = new ArrayList<>(userPreferences.get().getPreferredCategories());
                category = preferredCategories.get(random.nextInt(preferredCategories.size()));
            } else {
                List<Category> allCategories = categoryRepository.findAll();
                if (allCategories.isEmpty()) {
                    category = categoryRepository.save(Category.builder()
                            .name("General")
                            .description("General facts")
                            .build());
                } else {
                    category = allCategories.get(random.nextInt(allCategories.size()));
                }
            }
            
            // Get user interests if available
            String interests = userPreferences.map(pref -> pref.getInterests()).orElse("");
            
            // Get preferred language if available
            String language = userPreferences.map(pref -> pref.getPreferredLanguage()).orElse("English");
            
            // Generate fact
            String prompt = buildPersonalizedFactPrompt(category.getName(), interests, language);
            String factContent = callOpenAI(prompt);
            
            // Extract title and content
            String[] parts = factContent.split("\n", 2);
            String title = parts[0].replaceFirst("Title: ", "");
            String content = parts.length > 1 ? parts[1].replaceFirst("Content: ", "") : factContent;
            
            // Save fact to database
            Fact fact = Fact.builder()
                    .title(title)
                    .content(content)
                    .category(category)
                    .source("AI Generated")
                    .isPublished(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            Fact savedFact = factRepository.save(fact);
            
            return FactResponse.builder()
                    .id(savedFact.getId())
                    .title(savedFact.getTitle())
                    .content(savedFact.getContent())
                    .category(mapToCategoryResponse(savedFact.getCategory()))
                    .source(savedFact.getSource())
                    .isPublished(savedFact.isPublished())
                    .createdAt(savedFact.getCreatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error generating daily fact", e);
            throw new RuntimeException("Error generating daily fact: " + e.getMessage());
        }
    }
    
    private String buildFactGenerationPrompt(String topic, String language) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate an interesting and educational fact about ").append(topic);
        
        if (language != null && !language.isEmpty() && !language.equalsIgnoreCase("english")) {
            prompt.append(" in ").append(language);
        }
        
        prompt.append(". Format the response with a title and content section.");
        prompt.append("\nTitle: [Interesting title here]");
        prompt.append("\nContent: [Detailed fact content here]");
        
        return prompt.toString();
    }
    
    private String buildNewsRelevancePrompt(News news, String userInterests) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze if the following news article is relevant and interesting based on ");
        
        if (userInterests != null && !userInterests.isEmpty()) {
            prompt.append("these interests: ").append(userInterests);
        } else {
            prompt.append("general interest and importance");
        }
        
        prompt.append(".\n\nNews Title: ").append(news.getTitle());
        prompt.append("\nNews Summary: ").append(news.getSummary());
        prompt.append("\nNews Source: ").append(news.getSource());
        
        if (news.getCategory() != null) {
            prompt.append("\nCategory: ").append(news.getCategory().getName());
        }
        
        prompt.append("\n\nIs this news relevant? Please explain why or why not in 2-3 sentences.");
        
        return prompt.toString();
    }
    
    private String buildPersonalizedFactPrompt(String category, String interests, String language) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate an interesting and educational fact about ").append(category);
        
        if (interests != null && !interests.isEmpty()) {
            prompt.append(" related to these interests if possible: ").append(interests);
        }
        
        if (language != null && !language.isEmpty() && !language.equalsIgnoreCase("english")) {
            prompt.append(". Generate the fact in ").append(language);
        }
        
        prompt.append(". Format the response with a title and content section.");
        prompt.append("\nTitle: [Interesting title here]");
        prompt.append("\nContent: [Detailed fact content here]");
        
        return prompt.toString();
    }
    
    private String callOpenAI(String prompt) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", "You are a helpful assistant that provides interesting facts and analyzes news relevance."));
        messages.add(new ChatMessage("user", prompt));
        
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(messages)
                .model(model)
                .maxTokens(500)
                .build();
        
        return openAiService.createChatCompletion(completionRequest).getChoices().get(0).getMessage().getContent();
    }
    
    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
} 