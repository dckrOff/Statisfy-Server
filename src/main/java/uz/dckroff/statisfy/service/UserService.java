package uz.dckroff.statisfy.service;

import uz.dckroff.statisfy.dto.auth.AuthResponse;
import uz.dckroff.statisfy.dto.auth.LoginRequest;
import uz.dckroff.statisfy.dto.auth.RegisterRequest;
import uz.dckroff.statisfy.dto.user.UpdateProfileRequest;
import uz.dckroff.statisfy.dto.user.UserProfileResponse;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserProfileResponse getCurrentUserProfile();
    UserProfileResponse updateProfile(UpdateProfileRequest request);
} 