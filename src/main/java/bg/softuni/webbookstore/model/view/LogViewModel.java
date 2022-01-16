package bg.softuni.webbookstore.model.view;

import bg.softuni.webbookstore.model.entity.enums.OrderStatusEnum;

import java.time.ZonedDateTime;

public class LogViewModel {

    private OrderStatusEnum status;
    private ZonedDateTime changeTime;

    public LogViewModel() {
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public LogViewModel setStatus(OrderStatusEnum status) {
        this.status = status;
        return this;
    }

    public ZonedDateTime getChangeTime() {
        return changeTime;
    }

    public LogViewModel setChangeTime(ZonedDateTime changeTime) {
        this.changeTime = changeTime;
        return this;
    }
}
