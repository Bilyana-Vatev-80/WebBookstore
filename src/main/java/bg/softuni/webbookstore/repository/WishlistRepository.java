package bg.softuni.webbookstore.repository;

import bg.softuni.webbookstore.model.entity.WishlistItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistItemEntity, Long> {

    List<WishlistItemEntity> findAllByCustomerUsername(String username);

    Optional<WishlistItemEntity> findByBookIdAndCustomerUsername(Long bookId, String username);

    @Transactional
    void deleteAllByBookId(Long bookId);
}
