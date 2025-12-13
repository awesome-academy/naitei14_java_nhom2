package vn.sun.membermanagementsystem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response chung cho các thông báo")
public class MessageResponse {
    
    @Schema(description = "Nội dung thông báo", example = "User registered successfully")
    private String message;
    
    @Schema(description = "Trạng thái thành công hay thất bại", example = "true")
    private boolean success;
    
    public MessageResponse(String message) {
        this.message = message;
        this.success = true;
    }
}
