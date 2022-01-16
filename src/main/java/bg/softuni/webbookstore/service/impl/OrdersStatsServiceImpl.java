package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.model.entity.OrderEntity;
import bg.softuni.webbookstore.model.view.OrdersStatsView;
import bg.softuni.webbookstore.repository.OrderRepository;
import bg.softuni.webbookstore.service.OrdersStatsService;
import bg.softuni.webbookstore.web.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static bg.softuni.webbookstore.constant.GlobalConstants.*;

@Service
public class OrdersStatsServiceImpl implements OrdersStatsService {

    private final OrderRepository orderRepository;
    private int ordersAboveFiftyBGN, ordersBelowFiftyBGN;

    public OrdersStatsServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void onRequest(String requestURI) {
        if (requestURI.contains(ORDER_STATS_URI)) {
            OrderEntity lastOrder = orderRepository
                    .findTopByOrderByOrderTimeDesc()
                    .orElseThrow(() ->
                            new ObjectNotFoundException(OBJECT_NAME_ORDER));

            if (lastOrder.getPrice().compareTo(BigDecimal.valueOf(50)) >= 0) {
                ordersAboveFiftyBGN++;
            } else {
                ordersBelowFiftyBGN++;
            }
        }
    }

    @Override
    public OrdersStatsView getOrdersStats() {
        return new OrdersStatsView(ordersAboveFiftyBGN, ordersBelowFiftyBGN);
    }
}
