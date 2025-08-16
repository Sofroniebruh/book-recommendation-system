package com.example.spring_backend.book_related.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Rating r WHERE r.book.id = :bookId")
    Double getAverageRating(@Param("bookId") Long id);

    Optional<Rating> findRatingByBookIdAndUserId(Long bookId, Long userId);
}
