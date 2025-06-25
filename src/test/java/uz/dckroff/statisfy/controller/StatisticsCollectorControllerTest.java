package uz.dckroff.statisfy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uz.dckroff.statisfy.dto.ApiResponse;
import uz.dckroff.statisfy.dto.statistics.StatisticsCollectorResponse;
import uz.dckroff.statisfy.service.StatisticsCollectorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class StatisticsCollectorControllerTest {

    @Mock
    private StatisticsCollectorService statisticsCollectorService;

    @InjectMocks
    private StatisticsCollectorController statisticsCollectorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void runAllCollectors_shouldReturnSuccessResponse() {
        // Arrange
        StatisticsCollectorResponse response = new StatisticsCollectorResponse();
        response.setStatisticsCollected(100);
        response.setSource("All Sources");
        response.setCategory("All Categories");
        response.setSuccess(true);
        response.setMessage("Успешно собрано 100 статистических данных из всех источников");

        when(statisticsCollectorService.runAllCollectors()).thenReturn(response);

        // Act
        ResponseEntity<ApiResponse> result = statisticsCollectorController.runAllCollectors();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().isSuccess());
        verify(statisticsCollectorService, times(1)).runAllCollectors();
    }

    @Test
    void collectPopulationStatistics_shouldReturnSuccessResponse() {
        // Arrange
        StatisticsCollectorResponse response = new StatisticsCollectorResponse();
        response.setStatisticsCollected(25);
        response.setSource("World Bank API");
        response.setCategory("Население");
        response.setSuccess(true);
        response.setMessage("Успешно собрано 25 статистических данных о населении");

        when(statisticsCollectorService.collectPopulationStatistics()).thenReturn(response);

        // Act
        ResponseEntity<ApiResponse> result = statisticsCollectorController.collectPopulationStatistics();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().isSuccess());
        verify(statisticsCollectorService, times(1)).collectPopulationStatistics();
    }

    @Test
    void collectEconomicStatistics_shouldReturnSuccessResponse() {
        // Arrange
        StatisticsCollectorResponse response = new StatisticsCollectorResponse();
        response.setStatisticsCollected(30);
        response.setSource("Open Exchange Rates API");
        response.setCategory("Экономика");
        response.setSuccess(true);
        response.setMessage("Успешно собрано 30 экономических статистических данных");

        when(statisticsCollectorService.collectEconomicStatistics()).thenReturn(response);

        // Act
        ResponseEntity<ApiResponse> result = statisticsCollectorController.collectEconomicStatistics();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().isSuccess());
        verify(statisticsCollectorService, times(1)).collectEconomicStatistics();
    }

    @Test
    void collectHealthStatistics_shouldReturnSuccessResponse() {
        // Arrange
        StatisticsCollectorResponse response = new StatisticsCollectorResponse();
        response.setStatisticsCollected(15);
        response.setSource("Health Data API");
        response.setCategory("Здравоохранение");
        response.setSuccess(true);
        response.setMessage("Успешно собрано 15 статистических данных о здравоохранении");

        when(statisticsCollectorService.collectHealthStatistics()).thenReturn(response);

        // Act
        ResponseEntity<ApiResponse> result = statisticsCollectorController.collectHealthStatistics();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().isSuccess());
        verify(statisticsCollectorService, times(1)).collectHealthStatistics();
    }

    @Test
    void collectEducationStatistics_shouldReturnSuccessResponse() {
        // Arrange
        StatisticsCollectorResponse response = new StatisticsCollectorResponse();
        response.setStatisticsCollected(20);
        response.setSource("World Bank API");
        response.setCategory("Образование");
        response.setSuccess(true);
        response.setMessage("Успешно собрано 20 статистических данных об образовании");

        when(statisticsCollectorService.collectEducationStatistics()).thenReturn(response);

        // Act
        ResponseEntity<ApiResponse> result = statisticsCollectorController.collectEducationStatistics();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().isSuccess());
        verify(statisticsCollectorService, times(1)).collectEducationStatistics();
    }

    @Test
    void collectEnvironmentStatistics_shouldReturnSuccessResponse() {
        // Arrange
        StatisticsCollectorResponse response = new StatisticsCollectorResponse();
        response.setStatisticsCollected(10);
        response.setSource("World Bank API");
        response.setCategory("Экология");
        response.setSuccess(true);
        response.setMessage("Успешно собрано 10 статистических данных об экологии");

        when(statisticsCollectorService.collectEnvironmentStatistics()).thenReturn(response);

        // Act
        ResponseEntity<ApiResponse> result = statisticsCollectorController.collectEnvironmentStatistics();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue((Boolean) result.getBody().isSuccess());
        verify(statisticsCollectorService, times(1)).collectEnvironmentStatistics();
    }
} 