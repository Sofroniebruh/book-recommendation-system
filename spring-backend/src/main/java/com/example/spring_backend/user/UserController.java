package com.example.spring_backend.user;

import com.example.spring_backend.user.records.BookReadRequest;
import com.example.spring_backend.user.records.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUsername(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserDtoById(user.getId()));
    }

    @PutMapping("/{id}/read-books")
    public ResponseEntity<UserDto> updateListOfReadBooks(
            @PathVariable Long id,
            @Valid
            @RequestBody BookReadRequest bookReadRequest) {
        return ResponseEntity.ok((userService.updateUserBookList(id, bookReadRequest)));
    }

    @DeleteMapping("/{id}/read-books")
    public ResponseEntity<UserDto> deleteFromReadBooks(
            @PathVariable Long id,
            @RequestBody
            @Valid
            BookReadRequest bookReadRequest
    ) {
        return ResponseEntity.ok(userService.deleteFromUserRead(id, bookReadRequest));
    }
}
