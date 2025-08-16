// UNCOMMENT IF YOU NEED TO SEED THE DB WITH DATA

//package com.example.spring_backend.book_related;
//
//import com.example.spring_backend.book_related.book.Book;
//import com.example.spring_backend.book_related.book.BookRepository;
//import com.example.spring_backend.book_related.rating.Rating;
//import com.example.spring_backend.book_related.rating.RatingRepository;
//import com.example.spring_backend.book_related.rating.RatingService;
//import com.example.spring_backend.user.User;
//import com.example.spring_backend.user.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.nio.file.Paths;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class DataSeeder implements CommandLineRunner {
//
//    private final BookRepository bookRepository;
//    private final UserRepository userRepository;
//    private final RatingRepository ratingRepository;
//    private final RatingService ratingService;
//    public Map<Long, User> userCache = new HashMap<>();
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        String booksPath = Paths.get("src/main/resources/data/books.csv").toString();
//        String ratingsPath = Paths.get("src/main/resources/data/ratings.csv").toString();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(booksPath))) {
//            String line = br.readLine();
//            while ((line = br.readLine()) != null) {
//                String[] fields = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
//                Book book = Book.builder()
//                        .title(fields[10].replace("\"", ""))
//                        .authors(fields[7].replace("\"", ""))
//                        .isbn(fields[5])
//                        .originalPublicationYear(parseYear(fields[8]))
//                        .imageUrl(fields[21])
//                        .build();
//                bookRepository.save(book);
//            }
//        }
//
//        try (BufferedReader br = new BufferedReader(new FileReader(ratingsPath))) {
//            String line = br.readLine();
//            while ((line = br.readLine()) != null) {
//                String[] fields = line.split(",");
//                Long csvUserId = Long.parseLong(fields[1]);
//                Long bookId = Long.parseLong(fields[0]);
//                Integer ratingValue = Integer.parseInt(fields[2]);
//
//                User user = userCache.computeIfAbsent(csvUserId, id ->
//                        userRepository.save(User.builder().username("User_" + id).isFromDataset(true).build())
//                );
//
//                Book book = bookRepository.findById(bookId).orElse(null);
//                if (book != null) {
//                    Rating rating = Rating.builder()
//                            .user(user)
//                            .book(book)
//                            .rating(ratingValue)
//                            .build();
//                    ratingRepository.save(rating);
//                }
//            }
//        }
//
//        System.out.println("Dataset import completed");
//    }
//
//    private Integer parseYear(String yearStr) {
//        try {
//            return (int) Double.parseDouble(yearStr);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}
//
