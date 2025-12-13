package vn.sun.membermanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.sun.membermanagementsystem.dto.response.MessageResponse;
import vn.sun.membermanagementsystem.dto.response.UserProfileDetailDTO;
import vn.sun.membermanagementsystem.exception.ResourceNotFoundException;
import vn.sun.membermanagementsystem.services.UserService;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "User Profile", description = "APIs for managing user profile information")
public class UserProfileController {

    private final UserService userService;


    @Operation(
        summary = "Get user profile by ID",
        description = "Retrieves complete user profile including basic info, current team, position, active projects, and skills. " +
                      "Can be used by admin to view any user's profile or by users to view team members' profiles."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserProfileDetailDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MessageResponse.class)
            )
        )
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getUserProfileById(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("API: Fetching profile for user ID: {}", id);
        
        try {
            UserProfileDetailDTO profile = userService.getUserDetailById(id);
            return ResponseEntity.ok(profile);
        } catch (ResourceNotFoundException e) {
            log.warn("User not found with ID: {}", id);
            return ResponseEntity
                .status(404)
                .body(new MessageResponse(e.getMessage(), false));
        }
    }
}
