package com.smit.RealTimeChat.controller;
import com.smit.RealTimeChat.dto.PublicProfileResponse;
import com.smit.RealTimeChat.dto.UpdateProfileRequest;
import com.smit.RealTimeChat.dto.UserProfileResponse;
import com.smit.RealTimeChat.dto.UserResponse;
import com.smit.RealTimeChat.repository.UserRepository;
import com.smit.RealTimeChat.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserResponse response = userService.getCurrentUser(email);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(Authentication authentication) {
        String currentEmail = authentication.getName();
        List<UserResponse> users = userRepository.findAll().stream()
                .filter(user -> !user.getEmail().equalsIgnoreCase(currentEmail))
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        UserProfileResponse response = userService.getProfile(email);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        UserProfileResponse response = userService.updateProfile(email, request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/profile/{email}")
    public ResponseEntity<PublicProfileResponse> getPublicProfile(@PathVariable String email) {
        PublicProfileResponse response = userService.getPublicProfile(email);
        return ResponseEntity.ok(response);
    }
    @PostMapping(value = "/profile/image", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        try {
            String email = authentication.getName();
            UserProfileResponse response = userService.uploadProfileImage(email, file);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to save image"));
        }
    }
}