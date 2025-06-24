package uz.dckroff.statisfy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uz.dckroff.statisfy.dto.FactDTO;
import uz.dckroff.statisfy.exception.ResourceNotFoundException;
import uz.dckroff.statisfy.model.Category;
import uz.dckroff.statisfy.model.Fact;
import uz.dckroff.statisfy.repository.CategoryRepository;
import uz.dckroff.statisfy.repository.FactRepository;
import uz.dckroff.statisfy.service.impl.FactServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FactServiceTest {

    @Mock
    private FactRepository factRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private FactServiceImpl factService;

    private Fact testFact;
    private FactDTO testFactDTO;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(UUID.randomUUID())
                .name("Science")
                .description("Scientific facts")
                .build();

        testFact = Fact.builder()
                .id(UUID.randomUUID())
                .title("Test Fact")
                .content("This is a test fact content")
                .category(testCategory)
                .source("Test Source")
                .isPublished(true)
                .createdAt(LocalDateTime.now())
                .build();

        testFactDTO = FactDTO.builder()
                .title("Test Fact")
                .content("This is a test fact content")
                .categoryId(testCategory.getId())
                .source("Test Source")
                .isPublished(true)
                .build();
    }

    @Test
    void getAllFacts_ReturnsPageOfFacts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Fact> factsPage = new PageImpl<>(List.of(testFact), pageable, 1);
        when(factRepository.findAll(pageable)).thenReturn(factsPage);

        // Act
        Page<Fact> result = factService.getAllFacts(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testFact.getId(), result.getContent().get(0).getId());
        verify(factRepository, times(1)).findAll(pageable);
    }

    @Test
    void getFactById_ExistingFact_ReturnsFact() {
        // Arrange
        UUID factId = testFact.getId();
        when(factRepository.findById(factId)).thenReturn(Optional.of(testFact));

        // Act
        Fact result = factService.getFactById(factId);

        // Assert
        assertNotNull(result);
        assertEquals(factId, result.getId());
        assertEquals(testFact.getTitle(), result.getTitle());
        verify(factRepository, times(1)).findById(factId);
    }

    @Test
    void getFactById_NonExistingFact_ThrowsException() {
        // Arrange
        UUID factId = UUID.randomUUID();
        when(factRepository.findById(factId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> factService.getFactById(factId));
        verify(factRepository, times(1)).findById(factId);
    }

    @Test
    void createFact_ValidData_ReturnsSavedFact() {
        // Arrange
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
        when(factRepository.save(any(Fact.class))).thenAnswer(invocation -> {
            Fact fact = invocation.getArgument(0);
            fact.setId(UUID.randomUUID());
            return fact;
        });

        // Act
        Fact result = factService.createFact(testFactDTO);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(testFactDTO.getTitle(), result.getTitle());
        assertEquals(testFactDTO.getContent(), result.getContent());
        assertEquals(testCategory, result.getCategory());
        verify(categoryRepository, times(1)).findById(testCategory.getId());
        verify(factRepository, times(1)).save(any(Fact.class));
    }

    @Test
    void updateFact_ValidData_ReturnsUpdatedFact() {
        // Arrange
        UUID factId = testFact.getId();
        when(factRepository.findById(factId)).thenReturn(Optional.of(testFact));
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
        when(factRepository.save(any(Fact.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FactDTO updateDTO = FactDTO.builder()
                .title("Updated Fact")
                .content("Updated content")
                .categoryId(testCategory.getId())
                .source("Updated Source")
                .isPublished(true)
                .build();

        // Act
        Fact result = factService.updateFact(factId, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(factId, result.getId());
        assertEquals(updateDTO.getTitle(), result.getTitle());
        assertEquals(updateDTO.getContent(), result.getContent());
        verify(factRepository, times(1)).findById(factId);
        verify(categoryRepository, times(1)).findById(testCategory.getId());
        verify(factRepository, times(1)).save(any(Fact.class));
    }

    @Test
    void deleteFact_ExistingFact_DeletesSuccessfully() {
        // Arrange
        UUID factId = testFact.getId();
        when(factRepository.findById(factId)).thenReturn(Optional.of(testFact));
        doNothing().when(factRepository).delete(testFact);

        // Act
        factService.deleteFact(factId);

        // Assert
        verify(factRepository, times(1)).findById(factId);
        verify(factRepository, times(1)).delete(testFact);
    }
} 