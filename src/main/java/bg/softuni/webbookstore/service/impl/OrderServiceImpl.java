package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.model.entity.OrderEntity;
import bg.softuni.webbookstore.model.entity.OrderItemEntity;
import bg.softuni.webbookstore.model.entity.UserEntity;
import bg.softuni.webbookstore.model.entity.enums.OrderStatusEnum;
import bg.softuni.webbookstore.model.view.CartItemViewModel;
import bg.softuni.webbookstore.model.view.OrderViewModel;
import bg.softuni.webbookstore.repository.BookRepository;
import bg.softuni.webbookstore.repository.OrderItemRepository;
import bg.softuni.webbookstore.repository.OrderRepository;
import bg.softuni.webbookstore.repository.UserRepository;
import bg.softuni.webbookstore.service.OrderService;
import bg.softuni.webbookstore.service.ShoppingCartService;
import bg.softuni.webbookstore.service.UserService;
import bg.softuni.webbookstore.utils.StringUtils;
import bg.softuni.webbookstore.web.exception.InvalidOrderException;
import bg.softuni.webbookstore.web.exception.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static bg.softuni.webbookstore.constant.GlobalConstants.*;

@Service
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final ShoppingCartService shoppingCartService;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, UserRepository userRepository, BookRepository bookRepository, UserService userService, ShoppingCartService shoppingCartService, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.shoppingCartService = shoppingCartService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public List<OrderViewModel> findAllOrders() {
        return orderRepository
                .findAllByOrderByOrderTimeDesc()
                .stream()
                .map(this::getOrderViewModel)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<OrderViewModel> findAllOrdersByCustomer(String username) {
        return orderRepository
                .findAllByCustomerUsernameOrderByOrderTimeDesc(username)
                .stream()
                .map(this::getOrderViewModel)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<OrderViewModel> findLastFiveOrdersByCustomer(String username) {
        return orderRepository
                .findTop5ByCustomerUsernameOrderByOrderTimeDesc(username)
                .stream()
                .map(this::getOrderViewModel)
                .collect(Collectors.toList());
    }

    @Override
    public Long createOrder(String username) {

        List<CartItemViewModel> itemsToOrder = shoppingCartService
                .getCartItemsByCustomer(username);

        if (itemsToOrder.size() == 0) {
            throw new InvalidOrderException(EMPTY_ORDER_ERROR_MESSAGE);
        }

        BigDecimal totalPrice = itemsToOrder
                .stream()
                .map(CartItemViewModel::calculatePrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.valueOf(0));

        OrderEntity orderEntity = new OrderEntity()
                .setOrderTime(LocalDateTime.now())
                .setPrice(totalPrice)
                .setStatus(OrderStatusEnum.ORDERED)
                .setCustomer(getUserEntity(username));

        OrderEntity newOrder = orderRepository.save(orderEntity);

        itemsToOrder
                .stream()
                .map(this::getOrderItemEntity)
                .map(orderItemEntity -> orderItemEntity.setOrder(newOrder))
                .forEach(orderItemRepository::save);

        shoppingCartService.deleteOrderedCardItems(username);

        return newOrder.getId();
    }

    @Transactional
    @Override
    public Optional<OrderViewModel> findById(Long id) {
        return orderRepository
                .findById(id)
                .map(this::getOrderViewModel);
    }

    @Override
    public boolean isOwner(String userName, Long id) {
        Optional<UserEntity> userOpt = userRepository
                .findByUsername(userName);
        Optional<OrderEntity> orderOpt = orderRepository
                .findById(id);

        if (userOpt.isEmpty() || orderOpt.isEmpty()) {
            return false;
        } else {
            OrderEntity orderEntity = orderOpt.get();

            return userService.isAdmin(userOpt.get().getUsername())
                    || orderEntity.getCustomer().getUsername().equals(userName);
        }
    }

    @Override
    public void updateStatus(Long orderId, OrderStatusEnum status) {
        OrderEntity orderEntity = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_ORDER));

        orderEntity.setStatus(status);
        orderRepository.save(orderEntity);
    }

    @Transactional
    @Override
    public void proceedOrder(Long id) {
        OrderEntity orderEntity = orderRepository
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_ORDER));

        OrderStatusEnum currentStatus = orderEntity.getStatus();
        if (!currentStatus.equals(OrderStatusEnum.DELIVERED)) {
            OrderStatusEnum newStatus = currentStatus.next();
            orderEntity.setStatus(newStatus);
            orderRepository.save(orderEntity);
        }
    }

    @Transactional
    @Override
    public boolean canChangeStatus(Long orderId) {
        OrderEntity orderEntity = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_ORDER));

        return orderEntity
                .getOrderedBooks()
                .stream()
                .anyMatch(orderItemEntity -> orderItemEntity.getBook().getActive())
                && !orderEntity.getStatus().equals(OrderStatusEnum.DELIVERED);
    }

    @Override
    public void deleteOrdersOlderThanOneYear() {
        LocalDateTime previousYear = LocalDateTime.now().minus(365, ChronoUnit.DAYS);
        orderRepository.deleteAllByOrderTimeBefore(previousYear);
    }

    @Override
    public Map<Integer, Integer> getOrderedBooksPerMonthMap() {
        Map<Integer, Integer> orderedBooksPerMonth = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            orderedBooksPerMonth.put(i, orderItemRepository.findOrderedBooksCountByMonth(i));
        }

        return orderedBooksPerMonth;
    }

    @Override
    public Map<Integer, BigDecimal> getIncomePerMonth() {
        Map<Integer, BigDecimal> incomePerMonth = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            incomePerMonth.put(i, orderRepository.findIncomePerMonth(i));
        }

        return incomePerMonth;
    }


    private OrderItemEntity getOrderItemEntity(CartItemViewModel cartItem) {
        return new OrderItemEntity()
                .setQuantity(cartItem.getQuantity())
                .setBook(bookRepository
                        .findByIdAndActiveTrue(cartItem.getBook().getId())
                        .orElseThrow(() ->
                                new ObjectNotFoundException(OBJECT_NAME_BOOK)));
    }

    private OrderViewModel getOrderViewModel(OrderEntity orderEntity) {
        return modelMapper
                .map(orderEntity, OrderViewModel.class)
                .setCustomerFullName(StringUtils.getFullNameAsString(
                        orderEntity.getCustomer().getFirstName(),
                        orderEntity.getCustomer().getLastName()));
    }

    private UserEntity getUserEntity(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_USER));
    }
}
