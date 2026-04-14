package com.example.finpay.account_service.controllers;


import com.example.finpay.account_service.dto.UserResponse;
import com.example.finpay.account_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    private final UserService userService;


    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable String id){
        UserResponse userResponse = userService.findById(id);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<UserResponse> findByEmail(@RequestParam String email){
        UserResponse userResponse = userService.findByEmail(email);
        return ResponseEntity.ok(userResponse);
    }
    


    @PatchMapping("/{id}/block")
    public ResponseEntity<Void> blockUser(@PathVariable String id) {
        userService.blockUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable String id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }
}
