package bg.softuni.webbookstore.web.interceptor;

import bg.softuni.webbookstore.service.OrdersStatsService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class OrdersStatsInterceptor implements HandlerInterceptor {

    private final OrdersStatsService ordersStatsService;

    public OrdersStatsInterceptor(OrdersStatsService ordersStatsService) {
        this.ordersStatsService = ordersStatsService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ordersStatsService.onRequest(request.getRequestURI());
    }
}
