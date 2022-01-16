package bg.softuni.webbookstore.config;

import bg.softuni.webbookstore.web.interceptor.OrdersStatsInterceptor;
import bg.softuni.webbookstore.web.interceptor.PagesViewsCountInterceptor;
import bg.softuni.webbookstore.web.interceptor.RequestsStatsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final RequestsStatsInterceptor requestsStatsInterceptor;
    private final OrdersStatsInterceptor ordersStatsInterceptor;
    private final PagesViewsCountInterceptor pagesViewsCountInterceptor;

    public WebConfiguration(RequestsStatsInterceptor requestsStatsInterceptor, OrdersStatsInterceptor ordersStatsInterceptor, PagesViewsCountInterceptor pagesViewsCountInterceptor) {
        this.requestsStatsInterceptor = requestsStatsInterceptor;
        this.ordersStatsInterceptor = ordersStatsInterceptor;
        this.pagesViewsCountInterceptor = pagesViewsCountInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestsStatsInterceptor);
        registry.addInterceptor(ordersStatsInterceptor);
        registry.addInterceptor(pagesViewsCountInterceptor);
    }
}
