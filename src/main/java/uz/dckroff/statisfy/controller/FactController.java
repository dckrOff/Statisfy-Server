package uz.dckroff.statisfy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.dckroff.statisfy.dto.fact.FactRequest;
import uz.dckroff.statisfy.dto.fact.FactResponse;
import uz.dckroff.statisfy.service.FactService;

import java.util.List;

@RestController
@RequestMapping("/api/facts")
@RequiredArgsConstructor
@Tag(name = "Факты", description = "API для работы с фактами")
public class FactController {

    private final FactService factService;

    @Operation(summary = "Получить все факты", description = "Возвращает страницу фактов с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение фактов"),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<FactResponse>> getAllFacts(
            @Parameter(description = "Параметры пагинации") @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(factService.getAllFacts(pageable));
    }

    @Operation(summary = "Получить факты по категории", description = "Возвращает страницу фактов определенной категории")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение фактов по категории"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<FactResponse>> getFactsByCategory(
            @Parameter(description = "ID категории") @PathVariable Long categoryId,
            @Parameter(description = "Параметры пагинации") @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(factService.getFactsByCategory(categoryId, pageable));
    }

    @Operation(summary = "Получить факт по ID", description = "Возвращает факт по указанному ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение факта"),
            @ApiResponse(responseCode = "404", description = "Факт не найден", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<FactResponse> getFactById(
            @Parameter(description = "ID факта") @PathVariable Long id
    ) {
        return ResponseEntity.ok(factService.getFactById(id));
    }

    @Operation(summary = "Получить последние факты", description = "Возвращает список последних добавленных фактов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение последних фактов"),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content)
    })
    @GetMapping("/recent")
    public ResponseEntity<List<FactResponse>> getRecentFacts() {
        return ResponseEntity.ok(factService.getRecentFacts());
    }

    @Operation(summary = "Создать новый факт", description = "Создает новый факт (только для администраторов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Факт успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FactResponse> createFact(
            @Parameter(description = "Данные факта") @Valid @RequestBody FactRequest request
    ) {
        return ResponseEntity.ok(factService.createFact(request));
    }

    @Operation(summary = "Обновить факт", description = "Обновляет существующий факт (только для администраторов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Факт успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content),
            @ApiResponse(responseCode = "404", description = "Факт не найден", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FactResponse> updateFact(
            @Parameter(description = "ID факта") @PathVariable Long id,
            @Parameter(description = "Обновленные данные факта") @Valid @RequestBody FactRequest request
    ) {
        return ResponseEntity.ok(factService.updateFact(id, request));
    }

    @Operation(summary = "Удалить факт", description = "Удаляет существующий факт (только для администраторов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Факт успешно удален"),
            @ApiResponse(responseCode = "404", description = "Факт не найден", content = @Content),
            @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен", content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFact(
            @Parameter(description = "ID факта") @PathVariable Long id
    ) {
        factService.deleteFact(id);
        return ResponseEntity.noContent().build();
    }
} 