package vn.sun.membermanagementsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request đăng ký tài khoản mới")
public class RegisterRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Tên người dùng", example = "Hoài Anh", required = true, minLength = 2, maxLength = 100)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Email đăng ký", example = "user@example.com", required = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "Mật khẩu", example = "password123", required = true, minLength = 6, format = "password")
    private String password;
    
    @Past(message = "Birthday must be in the past")
    @Schema(description = "Ngày sinh", example = "1990-01-15", required = false, format = "date")
    private LocalDate birthday;
}
