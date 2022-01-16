package bg.softuni.webbookstore.service;

import bg.softuni.webbookstore.model.entity.enums.OrderStatusEnum;
import bg.softuni.webbookstore.model.view.OrderViewModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {

    List<OrderViewModel> findAllOrders();

    List<OrderViewModel> findAllOrdersByCustomer(String username);

    List<OrderViewModel> findLastFiveOrdersByCustomer(String username);

    Long createOrder(String username);

    Optional<OrderViewModel> findById(Long id);

    boolean isOwner(String userName, Long id);

    void updateStatus(Long orderId, OrderStatusEnum status);

    void proceedOrder(Long id);

    boolean canChangeStatus(Long orderId);

    void deleteOrdersOlderThanOneYear();

    Map<Integer, Integer> getOrderedBooksPerMonthMap();

    Map<Integer, BigDecimal> getIncomePerMonth();
}
