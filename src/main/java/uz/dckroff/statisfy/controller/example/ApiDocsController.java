package uz.dckroff.statisfy.controller.example;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.dckroff.statisfy.dto.StandardResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/docs")
@Tag(name = "API Documentation", description = "Endpoints для проверки документации API")
public class ApiDocsController {

    @Operation(summary = "Проверка API документации", 
               description = "Endpoint для проверки работоспособности OpenAPI документации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", 
                         description = "Успешный ответ",
                         content = @Content(mediaType = "application/json", 
                                           schema = @Schema(implementation = StandardResponse.class)))
    })
    @GetMapping("/check")
    public ResponseEntity<StandardResponse<Map<String, Object>>> checkApiDocs() {
        Map<String, Object> data = new HashMap<>();
        data.put("swagger-ui", "/swagger-ui/index.html");
        data.put("api-docs", "/v3/api-docs");
        
        return ResponseEntity.ok(StandardResponse.success(data, "OpenAPI documentation is working correctly"));
    }
} 