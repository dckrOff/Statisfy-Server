package uz.dckroff.statisfy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.dckroff.statisfy.dto.UserDTO;
import uz.dckroff.statisfy.exception.ResourceNotFoundException;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.model.enums.Role;
import uz.dckroff.statisfy.repository.UserRepository;
import uz.dckroff.statisfy.service.impl.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;
    private final String TEST_USERNAME = "testuser";
    private final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .password("encoded_password")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        testUserDTO = UserDTO.builder()
                .username(TEST_USERNAME)
                .email(TEST_EMAIL)
                .build();
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() {
        // Arrange
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_NonExistingUser_ThrowsException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserByUsername_ExistingUser_ReturnsUser() {
        // Arrange
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserByUsername(TEST_USERNAME);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.getUsername());
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    void getUserByUsername_NonExistingUser_ThrowsException() {
        // Arrange
        String nonExistingUsername = "nonexistent";
        when(userRepository.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername(nonExistingUsername));
        verify(userRepository, times(1)).findByUsername(nonExistingUsername);
    }

    @Test
    void updateUser_ValidData_ReturnsUpdatedUser() {
        // Arrange
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO updateDTO = UserDTO.builder()
                .username("updated_username")
                .email("updated@example.com")
                .build();

        // Act
        User result = userService.updateUser(userId, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(updateDTO.getUsername(), result.getUsername());
        assertEquals(updateDTO.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }
} 