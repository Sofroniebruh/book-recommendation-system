package com.example.spring_backend.user;

import com.example.spring_backend.book_related.book.Book;
import com.example.spring_backend.book_related.book.BookRepository;
import com.example.spring_backend.book_related.book.custom_exceptions.BookNotFoundException;
import com.example.spring_backend.book_related.book.records.BookResponse;
import com.example.spring_backend.book_related.rating.RatingService;
import com.example.spring_backend.user.custom_exception.UserBookListException;
import com.example.spring_backend.user.custom_exception.UserNotFoundException;
import com.example.spring_backend.user.records.BookReadRequest;
import com.example.spring_backend.user.records.UserDto;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final RatingService ratingService;

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id:" + id + " not found"));
    }

    private Set<BookResponse> getBookResponseFromUserReadBooks(User user) {
        return user
                .getReadBooks()
                .stream()
                .map(book -> BookResponse.fromEntity(book, ratingService.getAverageRatingPerBookCheckedRounded(book.getId())))
                .collect(Collectors.toSet());
    }

    @Transactional
    public UserDto getUserDtoById(Long id) {
        User user = getUserById(id);

        return UserDto.fromEntity(user, getBookResponseFromUserReadBooks(user));
    }

    @Transactional
    public UserDto updateUserBookList(Long userId, BookReadRequest bookReadRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id:" + userId + " not found"));
        Book bookById = bookRepository.findById(bookReadRequest.bookId())
                .orElseThrow(() -> new BookNotFoundException("Book with id:" + bookReadRequest.bookId() + " not found"));

        if (user.getReadBooks().contains(bookById)) {
            throw new UserBookListException("Book with id: " + bookReadRequest.bookId() + " already in the list");
        }

        Double rating = ratingService.getAverageRatingPerBookCheckedRounded(bookById.getId());

        bookById.setRating(rating);
        user.getReadBooks().add(bookById);

        User savedUser = userRepository.save(user);

        return UserDto.fromEntity(savedUser, getBookResponseFromUserReadBooks(savedUser));
    }

    @Transactional
    public UserDto deleteFromUserRead(Long userId, BookReadRequest bookReadRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id:" + userId + " not found"));
        Book book = bookRepository.findById(bookReadRequest.bookId())
                .orElseThrow(() -> new BookNotFoundException("Book with id:" + bookReadRequest.bookId() + " not found"));

        if (user.getReadBooks().contains(book)) {
            user.getReadBooks().remove(book);
        } else {
            throw new UserBookListException("Book with id: " + bookReadRequest.bookId() + " not found in user list");
        }
        User savedUser = userRepository.save(user);

        return UserDto.fromEntity(savedUser, getBookResponseFromUserReadBooks(savedUser));
    }
}
