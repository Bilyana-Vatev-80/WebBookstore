package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.model.entity.enums.OrderStatusEnum;
import bg.softuni.webbookstore.model.view.CartItemViewModel;
import bg.softuni.webbookstore.model.view.OrderItemViewModel;
import bg.softuni.webbookstore.model.view.OrderViewModel;
import bg.softuni.webbookstore.service.LogService;
import bg.softuni.webbookstore.service.OrderService;
import bg.softuni.webbookstore.service.event.OrderStatusChangeEvent;
import bg.softuni.webbookstore.web.exception.InvalidOrderException;
import bg.softuni.webbookstore.web.exception.ObjectNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static bg.softuni.webbookstore.constant.GlobalConstants.*;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final LogService logService;
    private final ApplicationEventPublisher eventPublisher;

    public OrderController(OrderService orderService, LogService logService, ApplicationEventPublisher eventPublisher) {
        this.orderService = orderService;
        this.logService = logService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/all")
    public String allOrders(Model model) {
        model.addAttribute("orders", orderService.findAllOrders());

        return "orders";
    }

    @GetMapping("/my-orders")
    public String ordersByCustomer(Model model,
                                   @AuthenticationPrincipal UserDetails principal) {

        model.addAttribute("orders", orderService
                .findAllOrdersByCustomer(principal.getUsername()));

        return "orders";
    }

    @PreAuthorize("isOwner(#id)")
    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id,
                               Model model) {

        OrderViewModel orderViewModel = orderService
                .findById(id)
                .orElseThrow(() ->
                        new ObjectNotFoundException(OBJECT_NAME_ORDER));

        boolean canProceed = orderService
                .canChangeStatus(orderViewModel.getId());

        model.addAttribute("order", orderViewModel);
        model.addAttribute("canProceed", canProceed);
        model.addAttribute("orderLogs", logService.findOrderStatusChangeLogs(id));

        return "order-details";
    }

    @GetMapping("/create")
    public String createOrder(@AuthenticationPrincipal UserDetails principal) {

        Long orderId = orderService
                .createOrder(principal.getUsername());
        publishOrderStatusChangeEvent(orderId);

        return "redirect:/orders/" + orderId;
    }

    @ExceptionHandler(InvalidOrderException.class)
    public ModelAndView handleEmptyOrderExceptions(InvalidOrderException e) {
        ModelAndView modelAndView = new ModelAndView("errors/empty-order-error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
        return modelAndView;
    }

    @GetMapping("/change-status")
    public String changeStatus() {
        return "change-order-status";
    }

    @PostMapping("/change-status")
    public String changeStatusConfirm(@RequestParam Long orderId,
                                      @RequestParam String status) {

        if (orderService.canChangeStatus(orderId)) {
            orderService.updateStatus(orderId, OrderStatusEnum.valueOf(status.toUpperCase()));
            publishOrderStatusChangeEvent(orderId);
        } else {
            throw new InvalidOrderException(CANNOT_UPDATE_ORDER_STATUS_ERROR_MESSAGE);
        }

        return "redirect:/orders/" + orderId;
    }

    @GetMapping("/proceed/{id}")
    public String proceedOrder(@PathVariable Long id) {

        if (orderService.canChangeStatus(id)) {
            orderService.proceedOrder(id);
            publishOrderStatusChangeEvent(id);
        } else {
            throw new InvalidOrderException(CANNOT_UPDATE_ORDER_STATUS_ERROR_MESSAGE);
        }

        return "redirect:/orders/" + id;
    }

    private void publishOrderStatusChangeEvent(Long orderId) {
        OrderStatusChangeEvent event = new OrderStatusChangeEvent(this, orderId);
        eventPublisher.publishEvent(event);
    }
}
