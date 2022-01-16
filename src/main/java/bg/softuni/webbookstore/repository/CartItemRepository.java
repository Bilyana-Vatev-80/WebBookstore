package bg.softuni.webbookstore.repository;

import bg.softuni.webbookstore.model.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    List<CartItemEntity> findAllByCustomerUsername(String username);

    Optional<CartItemEntity> findByBookIdAndCustomerUsername(Long id, String username);

    @Transactional
    void deleteAllByCustomerUsername(String username);

    @Transactional
    void deleteAllByBookId(Long bookId);
}
