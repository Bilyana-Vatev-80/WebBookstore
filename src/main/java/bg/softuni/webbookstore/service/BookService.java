package bg.softuni.webbookstore.service;

import bg.softuni.webbookstore.model.binding.BookUpdateBindingModel;
import bg.softuni.webbookstore.model.entity.enums.CategoryEnum;
import bg.softuni.webbookstore.model.service.BookAddServiceModel;
import bg.softuni.webbookstore.model.service.BookUpdateServiceModel;
import bg.softuni.webbookstore.model.view.BookDetailViewModel;
import bg.softuni.webbookstore.model.view.BookSummaryViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookService {

    List<BookSummaryViewModel> findAllBooks();

    List<BookSummaryViewModel> findAllBooksOrderByTitle();

    List<BookSummaryViewModel> findAllBooksOrderByAuthor();

    List<BookSummaryViewModel> findAllBooksOrderByBestSelling();

    List<BookSummaryViewModel> findTopThreeNewestBooks();

    List<BookSummaryViewModel> findTopThreeMostPopularBooks();

    List<BookSummaryViewModel> findBooksByAuthor(Long id);

    List<BookSummaryViewModel> findBooksByPublishingHouse(Long id);

    List<BookSummaryViewModel> getWishListBooksByCustomer(String username);

    String findBookTitleById(Long id);

    Long add(BookAddServiceModel bookAddServiceModel) throws IOException;

    Optional<BookDetailViewModel> findBookDetails(Long id);

    Optional<BookUpdateBindingModel> findBookToEdit(Long id);

    Long update(BookUpdateServiceModel bookUpdateServiceModel) throws IOException;

    void delete(Long id);

    boolean existsByIsbn(String isbn);

    void increaseWithOneCopy(Long id);

    void decreaseWithOneCopy(Long id);

    List<String> findAllBookTitlesWithTwoOrLessCopies();

    Map<String, Integer> getBookCategoriesMap();
}
