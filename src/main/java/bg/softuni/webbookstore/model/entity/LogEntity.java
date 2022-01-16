package bg.softuni.webbookstore.model.entity;

import bg.softuni.webbookstore.model.entity.enums.OrderStatusEnum;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "logs")
public class LogEntity extends BaseEntity {

    @ManyToOne
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatusEnum status;

    @Column(nullable = false)
    private Instant changeTime;

    public LogEntity() {
    }

    public OrderEntity getOrder() {
        return order;
    }

    public LogEntity setOrder(OrderEntity order) {
        this.order = order;
        return this;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public LogEntity setStatus(OrderStatusEnum status) {
        this.status = status;
        return this;
    }

    public Instant getChangeTime() {
        return changeTime;
    }

    public LogEntity setChangeTime(Instant changeTime) {
        this.changeTime = changeTime;
        return this;
    }
}
