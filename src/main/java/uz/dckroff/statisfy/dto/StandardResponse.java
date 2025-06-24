package uz.dckroff.statisfy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Стандартный формат ответа API")
public class StandardResponse<T> {

    @Schema(description = "Статус ответа (success/error)", example = "success")
    private String status;

    @Schema(description = "Сообщение", example = "Операция выполнена успешно")
    private String message;

    @Schema(description = "Код ответа", example = "200")
    private Integer code;

    @Schema(description = "Данные ответа")
    private T data;

    @Schema(description = "Временная метка ответа")
    private LocalDateTime timestamp;

    public static <T> StandardResponse<T> success(T data) {
        return StandardResponse.<T>builder()
                .status("success")
                .code(200)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> StandardResponse<T> success(T data, String message) {
        return StandardResponse.<T>builder()
                .status("success")
                .message(message)
                .code(200)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static StandardResponse<Void> success(String message) {
        return StandardResponse.<Void>builder()
                .status("success")
                .message(message)
                .code(200)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static StandardResponse<Void> error(String message, Integer code) {
        return StandardResponse.<Void>builder()
                .status("error")
                .message(message)
                .code(code)
                .timestamp(LocalDateTime.now())
                .build();
    }
} 