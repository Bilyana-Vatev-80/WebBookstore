package bg.softuni.webbookstore.model.view;

public class OrdersStatsView {

    private int ordersAboveFiftyBGN;
    private int getOrdersBelowFiftyBGN;

    public OrdersStatsView(int ordersAboveFiftyBGN, int getOrdersBelowFiftyBGN) {
        this.ordersAboveFiftyBGN = ordersAboveFiftyBGN;
        this.getOrdersBelowFiftyBGN = getOrdersBelowFiftyBGN;
    }

    public int getTotalOrders() {
        return ordersAboveFiftyBGN + getOrdersBelowFiftyBGN;
    }

    public int getOrdersAboveFiftyBGN() {
        return ordersAboveFiftyBGN;
    }

    public int getOrdersBelowFiftyBGN() {
        return getOrdersBelowFiftyBGN;
    }
}
