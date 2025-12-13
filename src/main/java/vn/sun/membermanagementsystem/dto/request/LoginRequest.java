package vn.sun.membermanagementsystem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request đăng nhập")
public class LoginRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Email đăng nhập", example = "user@example.com", required = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Schema(description = "Mật khẩu", example = "password123", required = true, format = "password")
    private String password;
}
