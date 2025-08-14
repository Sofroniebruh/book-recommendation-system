package com.example.spring_backend.book_related.book;

import com.example.spring_backend.book_related.rating.Rating;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String title;
    @Column(columnDefinition = "TEXT")
    private String isbn;
    private Integer originalPublicationYear;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    @Column(columnDefinition = "TEXT")
    private String authors;
    private Double rating;
    @OneToMany
    private List<Rating> ratings;
}
