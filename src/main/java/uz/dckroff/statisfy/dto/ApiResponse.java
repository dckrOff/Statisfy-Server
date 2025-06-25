package uz.dckroff.statisfy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Стандартный формат ответа API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private LocalDateTime timestamp;

    /**
     * Создает успешный ответ с данными
     * @param message сообщение
     * @param data данные
     * @return объект ApiResponse
     */
    public static ApiResponse success(String message, Object data) {
        return ApiResponse.builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Создает успешный ответ без данных
     * @param message сообщение
     * @return объект ApiResponse
     */
    public static ApiResponse success(String message) {
        return success(message, null);
    }

    /**
     * Создает ответ с ошибкой
     * @param message сообщение об ошибке
     * @return объект ApiResponse
     */
    public static ApiResponse error(String message) {
        return ApiResponse.builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Создает ответ с ошибкой и дополнительными данными
     * @param message сообщение об ошибке
     * @param data дополнительные данные об ошибке
     * @return объект ApiResponse
     */
    public static ApiResponse error(String message, Object data) {
        return ApiResponse.builder()
                .success(false)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
} 