package vn.sun.membermanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import vn.sun.membermanagementsystem.config.jwt.JwtUtils;
import vn.sun.membermanagementsystem.config.services.CustomUserDetailsService;
import vn.sun.membermanagementsystem.dto.request.LoginRequest;
import vn.sun.membermanagementsystem.dto.request.RegisterRequest;
import vn.sun.membermanagementsystem.dto.response.LoginResponse;
import vn.sun.membermanagementsystem.dto.response.MessageResponse;
import vn.sun.membermanagementsystem.dto.response.UserListItemDTO;
import vn.sun.membermanagementsystem.dto.response.UserProfileDetailDTO;
import vn.sun.membermanagementsystem.entities.User;
import vn.sun.membermanagementsystem.enums.UserRole;
import vn.sun.membermanagementsystem.enums.UserStatus;
import vn.sun.membermanagementsystem.repositories.UserRepository;
import vn.sun.membermanagementsystem.services.UserService;

import java.time.LocalDateTime;

/**
 * Controller xử lý authentication cho API (Client/Mobile)
 * Sử dụng JWT token (Stateless)
 * Endpoints:
 * - POST /api/v1/auth/login - Đăng nhập
 * - POST /api/v1/auth/register - Đăng ký
 * - GET /api/v1/auth/profile - Lấy thông tin user hiện tại
 * - POST /api/v1/auth/logout - Đăng xuất (client side)
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = " User Authentication", description = "APIs xác thực người dùng - Đăng nhập, đăng ký, quản lý profile")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthUserController {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;
    
    /**
     * API Login - Trả về JWT token
     * POST /api/v1/auth/login
     * Body: {"email": "user@example.com", "password": "password123"}
     * Response: {"token": "eyJ...", "type": "Bearer", "email": "...", "role": "USER", "userId": 1}
     */
    @Operation(
        summary = "Đăng nhập",
        description = "Xác thực người dùng bằng email và mật khẩu, trả về JWT token để sử dụng cho các API khác"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Đăng nhập thành công",
            content = @Content(
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "token": "eyJhbGc",
                          "type": "Bearer",
                          "email": "user@example.com",
                          "role": "MEMBER",
                          "userId": 1
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Email hoặc mật khẩu không đúng",
            content = @Content(
                schema = @Schema(implementation = MessageResponse.class),
                examples = @ExampleObject(
                    name = "Invalid Credentials",
                    value = """
                        {
                          "message": "Invalid email or password",
                          "success": false
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Lỗi server",
            content = @Content(
                schema = @Schema(implementation = MessageResponse.class),
                examples = @ExampleObject(
                    name = "Server Error",
                    value = """
                        {
                          "message": "An error occurred during login: Database connection failed",
                          "success": false
                        }
                        """
                )
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Thông tin đăng nhập (email và password)",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(
                        name = "Login Example",
                        value = """
                            {
                              "email": "user@example.com",
                              "password": "password123"
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 1. Xác thực email/password bằng AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );
            
            // 2. Set authentication vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 3. Generate JWT token từ UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateToken(userDetails);
            
            // 4. Lấy thông tin user từ DB
            User user = userDetailsService.getUserByEmail(userDetails.getUsername());
            
            // 5. Trả về response với token và thông tin user
            LoginResponse response = LoginResponse.builder()
                .token(jwt)
                .email(user.getEmail())
                .role(user.getRole().toString())
                .userId(user.getId())
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Invalid email or password", false));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("An error occurred during login: " + e.getMessage(), false));
        }
    }
    
    /**
     * API Register - Đăng ký user mới
     * POST /api/v1/auth/register
     * Body: {"name": "John", "email": "john@example.com", "password": "password123"}
     * Response: {"message": "User registered successfully", "success": true}
     */
    @Operation(
        summary = "Đăng ký tài khoản mới",
        description = "Tạo tài khoản người dùng mới với vai trò MEMBER. Email phải duy nhất."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Đăng ký thành công",
            content = @Content(
                schema = @Schema(implementation = MessageResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "message": "User registered successfully",
                          "success": true
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Email đã được sử dụng hoặc dữ liệu không hợp lệ",
            content = @Content(
                schema = @Schema(implementation = MessageResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Email Already Exists",
                        value = """
                            {
                              "message": "Email is already in use",
                              "success": false
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Validation Error",
                        value = """
                            {
                              "message": "Password must be at least 6 characters",
                              "success": false
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Lỗi server",
            content = @Content(
                schema = @Schema(implementation = MessageResponse.class),
                examples = @ExampleObject(
                    name = "Server Error",
                    value = """
                        {
                          "message": "An error occurred during registration: Internal server error",
                          "success": false
                        }
                        """
                )
            )
        )
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Thông tin đăng ký (name, email, password, birthday)",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = RegisterRequest.class),
                    examples = @ExampleObject(
                        name = "Register Example",
                        value = """
                            {
                              "name": "User",
                              "email": "user@example.com",
                              "password": "password123",
                              "birthday": "1990-01-15"
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // 1. Kiểm tra email đã tồn tại chưa
            if (userRepository.existsByEmailAndNotDeleted(registerRequest.getEmail())) {
                return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email is already in use", false));
            }
            
            // 2. Tạo user mới
            User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .birthday(registerRequest.getBirthday())
                .role(UserRole.MEMBER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            
            // 3. Lưu vào database
            userRepository.save(user);
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse("User registered successfully"));
            
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("An error occurred during registration: " + e.getMessage(), false));
        }
    }
    
    /**
     * API Register Admin - CHỈ DÙNG ĐỂ TẠO ADMIN ACCOUNT ĐẦU TIÊN
     * POST /api/v1/auth/register-admin
     * Body: {"name": "Admin", "email": "admin@example.com", "password": "admin123"}
     * 
     * ⚠️ SAU KHI TẠO ADMIN, NÊN XÓA HOẶC BẢO MẬT ENDPOINT NÀY
     */
    /*
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // ⚠️ CHỈ CHO PHÉP TẠO ADMIN NẾU CHƯA CÓ ADMIN NÀO
            List<User> existingAdmins = userRepository.findByRoleAndNotDeleted(UserRole.ADMIN);
            if (!existingAdmins.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Admin account already exists. Registration is disabled.", false));
            }
            
            // Kiểm tra email đã tồn tại chưa
            if (userRepository.existsByEmailAndNotDeleted(registerRequest.getEmail())) {
                return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email is already in use", false));
            }
            
            // Tạo admin user
            User admin = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.ADMIN) // ⭐ Set role là ADMIN
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            
            userRepository.save(admin);
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse("Admin registered successfully! Please login with your credentials."));
            
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("An error occurred during admin registration: " + e.getMessage(), false));
        }
    }
    */
    
    /**
     * API Get current user profile with full details
     * GET /api/v1/auth/profile
     * Header: Authorization: Bearer <token>
     * Response: Complete user profile including:
     * - Basic information (name, email, birthday, role, status, timestamps)
     * - Current position
     * - Active team
     * - Active projects
     * - Skills
     */
    @Operation(
        summary = "Lấy thông tin profile hiện tại",
        description = "Trả về thông tin chi tiết đầy đủ của người dùng đang đăng nhập bao gồm: thông tin cơ bản, vị trí, team, dự án và kỹ năng"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lấy thông tin thành công",
            content = @Content(
                schema = @Schema(implementation = UserProfileDetailDTO.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "id": 1,
                          "name": "User",
                          "email": "user@example.com",
                          "birthday": "1990-01-15",
                          "role": "MEMBER",
                          "status": "ACTIVE",
                          "createdAt": "2024-01-01T10:00:00",
                          "updatedAt": "2024-12-13T15:30:00",
                          "activeTeam": "Team Alpha",
                          "currentPosition": {
                            "id": 1,
                            "name": "Backend Developer",
                            "abbreviation": "BE"
                          },
                          "activeProjects": [
                            {
                              "id": 1,
                              "name": "Project X",
                              "abbreviation": "PX",
                              "status": "ACTIVE"
                            }
                          ],
                          "skills": [
                            {
                              "skillId": 1,
                              "skillName": "Java",
                              "level": "ADVANCED",
                              "usedYearNumber": 3.5
                            }
                          ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Chưa đăng nhập hoặc token không hợp lệ",
            content = @Content(
                schema = @Schema(implementation = MessageResponse.class),
                examples = @ExampleObject(
                    name = "Unauthorized",
                    value = """
                        {
                          "message": "Unauthorized",
                          "success": false
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Không tìm thấy thông tin người dùng",
            content = @Content(
                schema = @Schema(implementation = MessageResponse.class),
                examples = @ExampleObject(
                    name = "Not Found",
                    value = """
                        {
                          "message": "User not found",
                          "success": false
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Lỗi server",
            content = @Content(
                schema = @Schema(implementation = MessageResponse.class),
                examples = @ExampleObject(
                    name = "Server Error",
                    value = """
                        {
                          "message": "Error fetching user profile: Internal server error",
                          "success": false
                        }
                        """
                )
            )
        )
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse("Unauthorized", false));
        }
        
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userDetailsService.getUserByEmail(userDetails.getUsername());
            
            // Sử dụng UserService để lấy thông tin chi tiết đầy đủ
            UserProfileDetailDTO profile = userService.getUserDetailById(user.getId());
            
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("User not found", false));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error fetching user profile: " + e.getMessage(), false));
        }
    }
    
    /**
     * API Logout (Optional - Client side xóa token)
     * POST /api/v1/auth/logout
     * Note: Với JWT, logout thường xử lý ở client (xóa token)
     * Server side chỉ clear SecurityContext
     */
    @Operation(
        summary = "Đăng xuất",
        description = "Xóa authentication context. Client nên xóa JWT token sau khi gọi API này."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Đăng xuất thành công",
            content = @Content(
                schema = @Schema(implementation = MessageResponse.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "message": "Logged out successfully",
                          "success": true
                        }
                        """
                )
            )
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }
}
