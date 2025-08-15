package com.example.spring_backend.user;

import com.example.spring_backend.book_related.book.Book;
import com.example.spring_backend.book_related.book.BookRepository;
import com.example.spring_backend.book_related.book.custom_exceptions.BookNotFoundException;
import com.example.spring_backend.user.custom_exception.UserNotFoundException;
import com.example.spring_backend.user.records.BookReadRequest;
import com.example.spring_backend.user.records.UserDto;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id:" + id + " not found"));
    }

    @Transactional
    public UserDto getUserDtoById(Long id) {
        User user = getUserById(id);

        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto updateUserBookList(Long userId, BookReadRequest bookReadRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id:" + userId + " not found"));
        Book book = bookRepository.findById(bookReadRequest.bookId())
                .orElseThrow(() -> new BookNotFoundException("Book with id:" + bookReadRequest.bookId() + " not found"));

        user.getReadBooks().add(book);

        return UserDto.fromEntity(userRepository.save(user));
    }

    @Transactional
    public UserDto deleteFromUserRead(Long userId, BookReadRequest bookReadRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id:" + userId + " not found"));
        Book book = bookRepository.findById(bookReadRequest.bookId())
                .orElseThrow(() -> new BookNotFoundException("Book with id:" + bookReadRequest.bookId() + " not found"));

        user.getReadBooks().remove(book);

        return UserDto.fromEntity(userRepository.save(user));
    }
}
