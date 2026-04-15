package com.example.finpay.account_service.controllers;


import com.example.finpay.account_service.dto.user.UpdateUserRequest;
import com.example.finpay.account_service.dto.user.UserRequest;
import com.example.finpay.account_service.dto.user.UserResponse;
import com.example.finpay.account_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/users")
@Tag(name = "Users", description = "User management operations")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user", description = "Registers a new user in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest){
        UserResponse userResponse = userService.createUser(userRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @Operation(summary = "Get user by ID", description = "Returns a single user by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable String id){
        UserResponse userResponse = userService.findById(id);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Find user by email", description = "Returns a single user by their email address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/search")
    public ResponseEntity<UserResponse> findByEmail(@RequestParam String email){
        UserResponse userResponse = userService.findByEmail(email);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Update user", description = "Updates the data of an existing user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @RequestBody @Valid UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Block user", description = "Changes the user status to BLOCKED")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User blocked successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable String id) {
        userService.blockUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activate user", description = "Changes the user status to ACTIVE")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User activated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable String id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }
}
