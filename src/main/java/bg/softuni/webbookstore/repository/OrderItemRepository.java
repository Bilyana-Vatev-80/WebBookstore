package bg.softuni.webbookstore.repository;

import bg.softuni.webbookstore.model.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    @Query(value = "SELECT COUNT(items.id) FROM web_bookstore.order_items items " +
            "JOIN web_bookstore.orders orders " +
            "ON items.order_id = orders.id " +
            "WHERE MONTH(orders.order_time) = :month", nativeQuery = true)
    Integer findOrderedBooksCountByMonth(@Param("month") Integer month);
}
