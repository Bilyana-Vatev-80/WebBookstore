package bg.softuni.webbookstore.repository;

import bg.softuni.webbookstore.model.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findAllByOrderByOrderTimeDesc();

    List<OrderEntity> findAllByCustomerUsernameOrderByOrderTimeDesc(String customer_username);

    List<OrderEntity> findTop5ByCustomerUsernameOrderByOrderTimeDesc(String username);

    Optional<OrderEntity> findTopByOrderByOrderTimeDesc();

    @Transactional
    void deleteAllByOrderTimeBefore(LocalDateTime orderTime);

    @Query(value = "SELECT SUM(orders.price) FROM web_bookstore.orders AS orders " +
            "WHERE MONTH(orders.order_time) = :month", nativeQuery = true)
    BigDecimal findIncomePerMonth(@Param("month") Integer month);
}
