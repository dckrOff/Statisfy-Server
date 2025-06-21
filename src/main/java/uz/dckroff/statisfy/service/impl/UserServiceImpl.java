package uz.dckroff.statisfy.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.dckroff.statisfy.dto.auth.AuthResponse;
import uz.dckroff.statisfy.dto.auth.LoginRequest;
import uz.dckroff.statisfy.dto.auth.RegisterRequest;
import uz.dckroff.statisfy.dto.user.UpdateProfileRequest;
import uz.dckroff.statisfy.dto.user.UserProfileResponse;
import uz.dckroff.statisfy.exception.ResourceNotFoundException;
import uz.dckroff.statisfy.exception.UserAlreadyExistsException;
import uz.dckroff.statisfy.model.Role;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.repository.UserRepository;
import uz.dckroff.statisfy.security.JwtService;
import uz.dckroff.statisfy.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        
        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // Default role is USER
                .build();
        
        userRepository.save(user);
        
        // Generate JWT token
        String jwtToken = jwtService.generateToken(user);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        // Get user from repository
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Generate JWT token
        String jwtToken = jwtService.generateToken(user);
        
        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        User currentUser = getCurrentUser();
        
        return mapToUserProfileResponse(currentUser);
    }

    @Override
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        User currentUser = getCurrentUser();
        
        // Update fields if provided
        if (request.getUsername() != null && !request.getUsername().equals(currentUser.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UserAlreadyExistsException("Username already exists");
            }
            currentUser.setUsername(request.getUsername());
        }
        
        if (request.getEmail() != null && !request.getEmail().equals(currentUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email already exists");
            }
            currentUser.setEmail(request.getEmail());
        }
        
        if (request.getPassword() != null) {
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        userRepository.save(currentUser);
        
        return mapToUserProfileResponse(currentUser);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
} 