package com.example.spring_backend.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/private")
public class UserController {

    @GetMapping("/user")
    public ResponseEntity<?> getUsername(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user.getUsername());
    }
}
