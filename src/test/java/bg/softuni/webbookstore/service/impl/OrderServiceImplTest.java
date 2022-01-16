package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.model.entity.OrderEntity;
import bg.softuni.webbookstore.model.entity.UserEntity;
import bg.softuni.webbookstore.model.entity.UserRoleEntity;
import bg.softuni.webbookstore.model.entity.enums.OrderStatusEnum;
import bg.softuni.webbookstore.model.entity.enums.UserRoleEnum;
import bg.softuni.webbookstore.model.view.OrderViewModel;
import bg.softuni.webbookstore.repository.BookRepository;
import bg.softuni.webbookstore.repository.OrderItemRepository;
import bg.softuni.webbookstore.repository.OrderRepository;
import bg.softuni.webbookstore.repository.UserRepository;
import bg.softuni.webbookstore.service.OrderService;
import bg.softuni.webbookstore.service.ShoppingCartService;
import bg.softuni.webbookstore.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    private UserEntity testUser1;
    private UserEntity testUser2;
    private List<OrderEntity> testOrders;

    private OrderService orderServiceToTest;

    @Mock
    private OrderRepository mockOrderRepository;

    @Mock
    private OrderItemRepository mockOrderItemRepository;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private BookRepository mockBookRepository;

    @Mock
    private UserService mockUserService;

    @Mock
    private ShoppingCartService mockShoppingCartService;

    @Mock
    private ModelMapper mockModelMapper;


    @BeforeEach
    void setUp() {
        orderServiceToTest = new OrderServiceImpl(mockOrderRepository, mockOrderItemRepository,
                mockUserRepository, mockBookRepository,
                mockUserService, mockShoppingCartService, mockModelMapper);

        testUser1 = initTestUserOne();
        testUser2 = initTestUserTwo();
        testOrders = initOrders();
    }

    @Test
    void test_FindAllOrders_ReturnsCorrectly() {
        Mockito.when(mockOrderRepository.findAllByOrderByOrderTimeDesc())
                .thenReturn(testOrders);

        Mockito.when(mockModelMapper.map(testOrders.get(0), OrderViewModel.class))
                .thenReturn(new OrderViewModel()
                        .setOrderTime(testOrders.get(0).getOrderTime())
                        .setPrice(testOrders.get(0).getPrice())
                        .setStatus(testOrders.get(0).getStatus()));
        Mockito.when(mockModelMapper.map(testOrders.get(1), OrderViewModel.class))
                .thenReturn(new OrderViewModel()
                        .setOrderTime(testOrders.get(1).getOrderTime())
                        .setPrice(testOrders.get(1).getPrice())
                        .setStatus(testOrders.get(1).getStatus()));

        List<OrderViewModel> actualOrders = orderServiceToTest.findAllOrders();
        assertEquals(actualOrders.size(), 2);

        assertEquals(testOrders.get(0).getOrderTime(), actualOrders.get(0).getOrderTime());
        assertEquals(testOrders.get(1).getOrderTime(), actualOrders.get(1).getOrderTime());

        assertEquals(testOrders.get(0).getPrice(), actualOrders.get(0).getPrice());
        assertEquals(testOrders.get(1).getPrice(), actualOrders.get(1).getPrice());

        assertEquals(testOrders.get(0).getStatus(), actualOrders.get(0).getStatus());
        assertEquals(testOrders.get(1).getStatus(), actualOrders.get(1).getStatus());
    }

    @Test
    void test_FindAllOrdersByCustomer_ReturnsCorrectly() {
        Mockito.when(mockOrderRepository.findAllByCustomerUsernameOrderByOrderTimeDesc(testUser1.getUsername()))
                .thenReturn(List.of(testOrders.get(0)));

        Mockito.when(mockModelMapper.map(testOrders.get(0), OrderViewModel.class))
                .thenReturn(new OrderViewModel()
                        .setOrderTime(testOrders.get(0).getOrderTime())
                        .setPrice(testOrders.get(0).getPrice())
                        .setStatus(testOrders.get(0).getStatus()));


        List<OrderViewModel> actualOrders = orderServiceToTest.findAllOrdersByCustomer(testUser1.getUsername());
        assertEquals(actualOrders.size(), 1);

        assertEquals(testOrders.get(0).getOrderTime(), actualOrders.get(0).getOrderTime());
        assertEquals(testOrders.get(0).getPrice(), actualOrders.get(0).getPrice());
        assertEquals(testOrders.get(0).getStatus(), actualOrders.get(0).getStatus());
    }

    @Test
    void test_FindOrderById_ReturnsCorrectly() {
        Mockito.when(mockOrderRepository.findById(testOrders.get(0).getId()))
                .thenReturn(Optional.of(testOrders.get(0)));

        Mockito.when(mockModelMapper.map(testOrders.get(0), OrderViewModel.class))
                .thenReturn(new OrderViewModel()
                        .setOrderTime(testOrders.get(0).getOrderTime())
                        .setPrice(testOrders.get(0).getPrice())
                        .setStatus(testOrders.get(0).getStatus()));

        Optional<OrderViewModel> actualOpt = orderServiceToTest.findById(testOrders.get(0).getId());
        assertTrue(actualOpt.isPresent());

        OrderViewModel actual = actualOpt.get();
        assertEquals(testOrders.get(0).getOrderTime(), actual.getOrderTime());
        assertEquals(testOrders.get(0).getPrice(), actual.getPrice());
        assertEquals(testOrders.get(0).getStatus(), actual.getStatus());
    }

    private UserRoleEntity initAdminRole() {
        return new UserRoleEntity()
                .setRole(UserRoleEnum.ADMIN);
    }

    private UserEntity initTestUserOne() {
        return new UserEntity()
                .setFirstName("test1")
                .setLastName("test1")
                .setUsername("test1")
                .setEmail("test1@test1.bg")
                .setAddress("Sofia")
                .setPassword("12345")
                .setRoles(List.of(initAdminRole()));
    }

    private UserEntity initTestUserTwo() {
        return new UserEntity()
                .setFirstName("test2")
                .setLastName("test2")
                .setUsername("test2")
                .setEmail("test2@test2.bg")
                .setAddress("Sofia")
                .setPassword("12345")
                .setRoles(List.of(initAdminRole()));
    }

    private List<OrderEntity> initOrders() {
        OrderEntity order1 = new OrderEntity()
                .setOrderTime(LocalDateTime.now())
                .setPrice(BigDecimal.valueOf(10.00))
                .setStatus(OrderStatusEnum.ORDERED)
                .setCustomer(testUser1);

        OrderEntity order2 = new OrderEntity()
                .setOrderTime(LocalDateTime.now())
                .setPrice(BigDecimal.valueOf(20.00))
                .setStatus(OrderStatusEnum.ORDERED)
                .setCustomer(testUser2);

        return List.of(order2, order1);
    }

}