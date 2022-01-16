package bg.softuni.webbookstore.service.event;

import bg.softuni.webbookstore.model.entity.LogEntity;
import bg.softuni.webbookstore.model.entity.OrderEntity;
import bg.softuni.webbookstore.repository.LogRepository;
import bg.softuni.webbookstore.repository.OrderRepository;
import bg.softuni.webbookstore.web.exception.ObjectNotFoundException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static bg.softuni.webbookstore.constant.GlobalConstants.*;

@Component
public class OrderStatusChangeLogger {

    private final LogRepository logRepository;
    private final OrderRepository orderRepository;

    public OrderStatusChangeLogger(LogRepository logRepository, OrderRepository orderRepository) {
        this.logRepository = logRepository;
        this.orderRepository = orderRepository;
    }

    @EventListener(OrderStatusChangeEvent.class)
    public void onOrderStatusChange(OrderStatusChangeEvent event) {

        OrderEntity orderEntity = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_ORDER));

        LogEntity logEntity = new LogEntity()
                .setOrder(orderEntity)
                .setStatus(orderEntity.getStatus())
                .setChangeTime(Instant.now());

        logRepository.save(logEntity);
    }
}
