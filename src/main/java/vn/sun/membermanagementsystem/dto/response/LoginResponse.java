package vn.sun.membermanagementsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response trả về khi đăng nhập thành công")
public class LoginResponse {
    
    @Schema(description = "JWT token để sử dụng cho các API khác", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Builder.Default
    @Schema(description = "Loại token", example = "Bearer", defaultValue = "Bearer")
    private String type = "Bearer";
    
    @Schema(description = "Email của người dùng", example = "user@example.com")
    private String email;
    
    @Schema(description = "Vai trò của người dùng", example = "MEMBER", allowableValues = {"ADMIN", "MEMBER"})
    private String role;
    
    @Schema(description = "ID của người dùng", example = "1")
    private Long userId;
}
