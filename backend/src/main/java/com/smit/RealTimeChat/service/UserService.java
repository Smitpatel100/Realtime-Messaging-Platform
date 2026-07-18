package com.smit.RealTimeChat.service;
import com.smit.RealTimeChat.dto.LoginRequest;
import com.smit.RealTimeChat.dto.LoginResponse;
import com.smit.RealTimeChat.dto.PublicProfileResponse;
import com.smit.RealTimeChat.dto.RegisterRequest;
import com.smit.RealTimeChat.dto.UpdateProfileRequest;
import com.smit.RealTimeChat.dto.UserProfileResponse;
import com.smit.RealTimeChat.dto.UserResponse;
import com.smit.RealTimeChat.entity.User;
import com.smit.RealTimeChat.exception.EmailAlreadyExistsException;
import com.smit.RealTimeChat.exception.InvalidCredentialsException;
import com.smit.RealTimeChat.exception.UserNotFoundException;
import com.smit.RealTimeChat.repository.UserRepository;
import com.smit.RealTimeChat.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final String UPLOAD_DIR = "uploads";
    private static final String PUBLIC_BASE_URL = "http://localhost:8080/uploads/";
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    public User register(RegisterRequest request) {
        // Step 1: Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already in use: " + request.getEmail()
            );
        }
        // Step 2: Hash the password before storing it
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        // Step 3: Build the User entity from validated DTO data
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        // Step 4: Persist the user
        return userRepository.save(user);
    }
    public LoginResponse login(LoginRequest request) {
        // Step 1: Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        // Step 2 & 3: Compare raw password against the stored BCrypt hash
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        // Step 4: Generate JWT for the authenticated user
        String token = jwtService.generateToken(user.getEmail());
        // Step 5: Return token wrapped in response DTO
        return new LoginResponse(token);
    }
    public UserResponse getCurrentUser(String email) {
        // Step 1: Find user by email, throw if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        // Step 2: Map entity to UserResponse DTO (no password field)
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return mapToProfileResponse(user);
    }
    public PublicProfileResponse getPublicProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return new PublicProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getProfileImage()
        );
    }
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Email is intentionally never touched here
        user.setUsername(request.getUsername());
        user.setBio(request.getBio());

        if (request.getProfileImage() != null && !request.getProfileImage().isBlank()) {
            user.setProfileImage(request.getProfileImage());
        }

        User saved = userRepository.save(user);
        return mapToProfileResponse(saved);
    }
    public UserProfileResponse uploadProfileImage(String email, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File must be 5MB or smaller");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG, PNG, WEBP, or GIF images are allowed");
        }

        Files.createDirectories(Paths.get(UPLOAD_DIR));

        String extension = switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
        String filename = UUID.randomUUID() + extension;
        Path targetPath = Paths.get(UPLOAD_DIR, filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        user.setProfileImage(PUBLIC_BASE_URL + filename);
        User saved = userRepository.save(user);
        return mapToProfileResponse(saved);
    }
    private UserProfileResponse mapToProfileResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getProfileImage(),
                user.getCreatedAt()
        );
    }
}