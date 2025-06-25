package uz.dckroff.statisfy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uz.dckroff.statisfy.dto.ApiResponse;
import uz.dckroff.statisfy.dto.fact.FactCollectorResponse;
import uz.dckroff.statisfy.service.FactCollectorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FactCollectorControllerTest {

    @Mock
    private FactCollectorService factCollectorService;

    @InjectMocks
    private FactCollectorController factCollectorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void runAllCollectors_shouldReturnSuccessResponse() {
        // Arrange
        FactCollectorResponse response = new FactCollectorResponse();
        response.setFactsCollected(50);
        response.setSource("All Sources");
        response.setCategory("All Categories");
        response.setSuccess(true);
        response.setMessage("Успешно собрано 50 фактов из всех источников");

        when(factCollectorService.runAllCollectors()).thenReturn(response);

        // Act
        ResponseEntity<ApiResponse> result = factCollectorController.runAllCollectors();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().isSuccess());
        verify(factCollectorService, times(1)).runAllCollectors();
    }

    @Test
    void collectFromWikipedia_shouldReturnSuccessResponse() {
        // Arrange
        FactCollectorResponse response = new FactCollectorResponse();
        response.setFactsCollected(10);
        response.setSource("Wikipedia");
        response.setCategory("General");
        response.setSuccess(true);
        response.setMessage("Успешно собрано 10 фактов из Wikipedia");

        when(factCollectorService.collectFromWikipedia()).thenReturn(response);

        // Act
        ResponseEntity<ApiResponse> result = factCollectorController.collectFromWikipedia();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().isSuccess());
        verify(factCollectorService, times(1)).collectFromWikipedia();
    }

    @Test
    void collectFromNumbersApi_shouldReturnSuccessResponse() {
        // Arrange
        FactCollectorResponse response = new FactCollectorResponse();
        response.setFactsCollected(15);
        response.setSource("Numbers API");
        response.setCategory("Math");
        response.setSuccess(true);
        response.setMessage("Успешно собрано 15 фактов о числах");

        when(factCollectorService.collectFromNumbersApi()).thenReturn(response);

        // Act
        ResponseEntity<ApiResponse> result = factCollectorController.collectFromNumbersApi();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().isSuccess());
        verify(factCollectorService, times(1)).collectFromNumbersApi();
    }

    @Test
    void collectHistoricalFacts_shouldReturnSuccessResponse() {
        // Arrange
        FactCollectorResponse response = new FactCollectorResponse();
        response.setFactsCollected(8);
        response.setSource("History API");
        response.setCategory("History");
        response.setSuccess(true);
        response.setMessage("Успешно собрано 8 исторических фактов");

        when(factCollectorService.collectHistoricalFacts()).thenReturn(response);

        // Act
        ResponseEntity<ApiResponse> result = factCollectorController.collectHistoricalFacts();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().isSuccess());
        verify(factCollectorService, times(1)).collectHistoricalFacts();
    }

    @Test
    void collectScienceFacts_shouldReturnSuccessResponse() {
        // Arrange
        FactCollectorResponse response = new FactCollectorResponse();
        response.setFactsCollected(12);
        response.setSource("Space News API");
        response.setCategory("Science");
        response.setSuccess(true);
        response.setMessage("Успешно собрано 12 научных фактов");

        when(factCollectorService.collectScienceFacts()).thenReturn(response);

        // Act
        ResponseEntity<ApiResponse> result = factCollectorController.collectScienceFacts();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().isSuccess());
        verify(factCollectorService, times(1)).collectScienceFacts();
    }
} 