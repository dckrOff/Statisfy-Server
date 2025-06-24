package uz.dckroff.statisfy.service;

import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uz.dckroff.statisfy.dto.AIFactRequest;
import uz.dckroff.statisfy.dto.AINewsAnalysisRequest;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.repository.CategoryRepository;
import uz.dckroff.statisfy.service.impl.AIServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AIServiceTest {

    @Mock
    private OpenAiService openAiService;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AIServiceImpl aiService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(UUID.randomUUID())
                .name("Science")
                .description("Scientific facts")
                .build();

        ReflectionTestUtils.setField(aiService, "aiEnabled", true);
        ReflectionTestUtils.setField(aiService, "model", "gpt-3.5-turbo");
    }

    @Test
    void generateFact_ValidRequest_ReturnsGeneratedFact() {
        // Arrange
        AIFactRequest request = new AIFactRequest();
        request.setCategoryId(testCategory.getId());
        request.setPrompt("Generate a fact about space");

        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));

        ChatMessage responseMessage = new ChatMessage();
        responseMessage.setContent("The Sun is so large that approximately 1.3 million Earths could fit inside it.");

        ChatCompletionChoice choice = new ChatCompletionChoice();
        choice.setMessage(responseMessage);

        ChatCompletionResult result = new ChatCompletionResult();
        result.setChoices(List.of(choice));

        when(openAiService.createChatCompletion(any(ChatCompletionRequest.class))).thenReturn(result);

        // Act
        String generatedFact = aiService.generateFact(request);

        // Assert
        assertNotNull(generatedFact);
        assertEquals("The Sun is so large that approximately 1.3 million Earths could fit inside it.", generatedFact);
        verify(categoryRepository, times(1)).findById(testCategory.getId());
        verify(openAiService, times(1)).createChatCompletion(any(ChatCompletionRequest.class));
    }

    @Test
    void analyzeNewsRelevance_ValidRequest_ReturnsAnalysisResult() {
        // Arrange
        AINewsAnalysisRequest request = new AINewsAnalysisRequest();
        request.setTitle("New Breakthrough in Quantum Computing");
        request.setSummary("Scientists have achieved a major breakthrough in quantum computing technology.");
        request.setCategory("Technology");

        ChatMessage responseMessage = new ChatMessage();
        responseMessage.setContent("0.85");

        ChatCompletionChoice choice = new ChatCompletionChoice();
        choice.setMessage(responseMessage);

        ChatCompletionResult result = new ChatCompletionResult();
        result.setChoices(List.of(choice));

        when(openAiService.createChatCompletion(any(ChatCompletionRequest.class))).thenReturn(result);

        // Act
        double relevanceScore = aiService.analyzeNewsRelevance(request);

        // Assert
        assertEquals(0.85, relevanceScore, 0.01);
        verify(openAiService, times(1)).createChatCompletion(any(ChatCompletionRequest.class));
    }

    @Test
    void generateDailyFact_ValidCategory_ReturnsGeneratedFact() {
        // Arrange
        String category = "Science";

        ChatMessage responseMessage = new ChatMessage();
        responseMessage.setContent("The human brain has about 86 billion neurons.");

        ChatCompletionChoice choice = new ChatCompletionChoice();
        choice.setMessage(responseMessage);

        ChatCompletionResult result = new ChatCompletionResult();
        result.setChoices(List.of(choice));

        when(openAiService.createChatCompletion(any(ChatCompletionRequest.class))).thenReturn(result);

        // Act
        String dailyFact = aiService.generateDailyFact(category);

        // Assert
        assertNotNull(dailyFact);
        assertEquals("The human brain has about 86 billion neurons.", dailyFact);
        verify(openAiService, times(1)).createChatCompletion(any(ChatCompletionRequest.class));
    }

    @Test
    void aiDisabled_ReturnsDefaultResponses() {
        // Arrange
        ReflectionTestUtils.setField(aiService, "aiEnabled", false);
        
        AIFactRequest factRequest = new AIFactRequest();
        factRequest.setCategoryId(testCategory.getId());
        factRequest.setPrompt("Generate a fact about space");
        
        AINewsAnalysisRequest newsRequest = new AINewsAnalysisRequest();
        newsRequest.setTitle("New Breakthrough in Quantum Computing");
        newsRequest.setSummary("Scientists have achieved a major breakthrough in quantum computing technology.");
        newsRequest.setCategory("Technology");

        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));

        // Act
        String generatedFact = aiService.generateFact(factRequest);
        double relevanceScore = aiService.analyzeNewsRelevance(newsRequest);
        String dailyFact = aiService.generateDailyFact("Science");

        // Assert
        assertEquals("AI generation is currently disabled. Please try again later.", generatedFact);
        assertEquals(0.5, relevanceScore, 0.01);
        assertEquals("AI generation is currently disabled. Please try again later.", dailyFact);
        
        verify(openAiService, never()).createChatCompletion(any(ChatCompletionRequest.class));
    }
} 