package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.service.OrdersStatsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrdersStatsController {

    private final OrdersStatsService ordersStatsService;

    public OrdersStatsController(OrdersStatsService ordersStatsService) {
        this.ordersStatsService = ordersStatsService;
    }

    @GetMapping("/stats/orders")
    public String statistics(Model model) {
        model.addAttribute("stats", ordersStatsService.getOrdersStats());
        return "orders-stats";
    }
}
