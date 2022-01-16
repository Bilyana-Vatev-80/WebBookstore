package bg.softuni.webbookstore.repository;

import bg.softuni.webbookstore.model.entity.BookEntity;
import bg.softuni.webbookstore.model.entity.CategoryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    @EntityGraph(value = "bookEntity")
    List<BookEntity> findAllByActiveTrueOrderByAddedOnDesc();

    @EntityGraph(value = "bookEntity")
    List<BookEntity> findAllByActiveTrueOrderByTitleAscAddedOnDesc();

    @EntityGraph(value = "bookEntity")
    @Query("SELECT b FROM BookEntity b " +
            "WHERE b.active = true " +
            "ORDER BY b.author.firstName, b.author.lastName, b.addedOn DESC")
    List<BookEntity> findAllByActiveTrueOrderByAuthor();

    @EntityGraph(value = "bookEntity")
    @Query("SELECT b FROM BookEntity b " +
            "LEFT JOIN OrderItemEntity o ON b.id = o.book.id " +
            "WHERE b.active = true " +
            "GROUP BY b.id " +
            "ORDER BY COUNT(o.id) DESC, b.addedOn DESC")
    List<BookEntity> findAllByActiveTrueOrderByBestSelling();

    @EntityGraph(value = "bookEntity")
    List<BookEntity> findTop3NewestByActiveTrueOrderByAddedOnDesc();

    @EntityGraph(value = "bookEntity")
    @Query("SELECT b FROM BookEntity b " +
            "LEFT JOIN OrderItemEntity o ON b.id = o.book.id " +
            "LEFT JOIN ReviewEntity r ON b.id = r.book.id " +
            "WHERE b.active = true " +
            "GROUP BY b.id " +
            "ORDER BY COUNT(o.id) DESC, COUNT(r.id) DESC, b.addedOn DESC")
    List<BookEntity> findTop3MostPopularBySoldCopiesAndReviewsCount(Pageable pageable);

    @EntityGraph(value = "bookEntity")
    List<BookEntity> findAllByActiveTrueAndAuthorIdOrderByAddedOnDesc(Long id);

    @EntityGraph(value = "bookEntity")
    List<BookEntity> findAllByActiveTrueAndPublishingHouseIdOrderByAddedOnDesc(Long id);

    @EntityGraph(value = "bookEntity")
    Optional<BookEntity> findByIdAndActiveTrue(Long id);

    Optional<BookEntity> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    boolean existsByPictureId(Long pictureId);

    @Query("SELECT b.title FROM BookEntity b " +
            "WHERE b.copies <= 2 " +
            "ORDER BY b.title")
    List<String> findAllBookTitlesWithTwoOrLessCopies();

    @Query("SELECT COUNT(b.id) FROM BookEntity b " +
            "WHERE :category MEMBER b.categories")
    Integer findBooksCountByCategory(@Param("category") CategoryEntity category);
}
