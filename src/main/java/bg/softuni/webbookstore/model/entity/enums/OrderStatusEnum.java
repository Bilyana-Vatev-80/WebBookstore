package bg.softuni.webbookstore.model.entity.enums;

public enum OrderStatusEnum {
    ORDERED,
    RECEIVED,
    PACKAGED,
    SENT,
    DELIVERED;

    private static final OrderStatusEnum[] values = values();

    public OrderStatusEnum next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
