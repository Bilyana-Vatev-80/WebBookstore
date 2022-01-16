package bg.softuni.webbookstore.service.event;

import org.springframework.context.ApplicationEvent;

public class OrderStatusChangeEvent extends ApplicationEvent {

    private final Long orderId;

    public OrderStatusChangeEvent(Object source, Long orderId) {
        super(source);
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
