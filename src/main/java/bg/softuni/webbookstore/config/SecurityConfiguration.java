package bg.softuni.webbookstore.config;

import bg.softuni.webbookstore.service.OrderService;
import bg.softuni.webbookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Autowired
    private BookstoreMethodSecurityExpressionHandler bookstoreMethodSecurityExpressionHandler;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return bookstoreMethodSecurityExpressionHandler;
    }

    @Bean
    public BookstoreMethodSecurityExpressionHandler createExpressionHandler(UserService userService, OrderService orderService) {
        return new BookstoreMethodSecurityExpressionHandler(userService, orderService);
    }

}