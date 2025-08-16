package com.example.spring_backend.book_related.rating;

import com.example.spring_backend.book_related.rating.records.RatingRequest;
import com.example.spring_backend.book_related.rating.records.RatingResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingResponse> createRating(@Valid @RequestBody RatingRequest request) {
        return ratingService.saveRating(request);
    }
}
