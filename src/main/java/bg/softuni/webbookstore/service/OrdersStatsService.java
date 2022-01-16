package bg.softuni.webbookstore.service;

import bg.softuni.webbookstore.model.view.OrdersStatsView;

public interface OrdersStatsService {

    void onRequest(String requestURI);

    OrdersStatsView getOrdersStats();
}
