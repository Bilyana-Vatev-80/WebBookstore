package bg.softuni.webbookstore.config;

import bg.softuni.webbookstore.service.OrderService;
import bg.softuni.webbookstore.service.ReviewService;
import bg.softuni.webbookstore.service.UserService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

public class BookstoreMethodSecurityExpressionHandler extends
        DefaultMethodSecurityExpressionHandler {

    private final UserService userService;
    private final OrderService orderService;

    public BookstoreMethodSecurityExpressionHandler(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {

        OwnerSecurityExpressionRoot root = new OwnerSecurityExpressionRoot(authentication);

        root.setUserService(userService);
        root.setOrderService(orderService);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(new AuthenticationTrustResolverImpl());
        root.setRoleHierarchy(getRoleHierarchy());

        return root;
    }
}
