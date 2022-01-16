package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.model.entity.OrderEntity;
import bg.softuni.webbookstore.model.entity.UserEntity;
import bg.softuni.webbookstore.model.entity.UserRoleEntity;
import bg.softuni.webbookstore.model.entity.enums.OrderStatusEnum;
import bg.softuni.webbookstore.model.entity.enums.UserRoleEnum;
import bg.softuni.webbookstore.repository.OrderRepository;
import bg.softuni.webbookstore.repository.UserRepository;
import bg.softuni.webbookstore.repository.UserRoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private OrderRepository orderRepository;

    private UserRoleEntity adminRole;
    private UserRoleEntity userRole;
    private UserEntity testUser1;
    private UserEntity testUser2;
    private List<OrderEntity> testOrders;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        adminRole = initAdminRole();
        userRole = initUserRole();
        testUser1 = initTestUserOne();
        testUser2 = initTestUserTwo();

        testOrders = initTestOrders();
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
        userRoleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testUser1", roles = "ADMIN")
    void test_GetAllOrders_ReturnsCorrectly() throws Exception {
        mockMvc
                .perform(get("/orders/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @WithMockUser(username = "testUser1")
    void test_GetOrdersByCustomer_ReturnsCorrectly() throws Exception {
        mockMvc
                .perform(get("/orders/my-orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @WithMockUser(username = "testUser1")
    void test_GetOrderDetails_ReturnsCorrectly() throws Exception {
        mockMvc
                .perform(get("/orders/" + testOrders.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("order-details"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("canProceed"))
                .andExpect(model().attributeExists("orderLogs"));
    }

    @Test
    @WithMockUser(username = "testUser2")
    void test_GetOrderDetailsWithUnauthorizedUser_Throws() throws Exception {
        mockMvc
                .perform(get("/orders/" + testOrders.get(0).getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/unauthorized"));
    }


    private UserRoleEntity initAdminRole() {
        return userRoleRepository.save(new UserRoleEntity()
                .setRole(UserRoleEnum.ADMIN));
    }

    private UserRoleEntity initUserRole() {
        return userRoleRepository.save(new UserRoleEntity()
                .setRole(UserRoleEnum.USER));
    }

    private UserEntity initTestUserOne() {
        return userRepository.save(new UserEntity()
                .setFirstName("test1")
                .setLastName("test1")
                .setUsername("testUser1")
                .setEmail("test1@test1.bg")
                .setAddress("Sofia")
                .setPassword("12345")
                .setRoles(List.of(adminRole)));
    }

    private UserEntity initTestUserTwo() {
        return userRepository.save(new UserEntity()
                .setFirstName("test2")
                .setLastName("test2")
                .setUsername("testUser2")
                .setEmail("test2@test2.bg")
                .setAddress("Sofia")
                .setPassword("12345")
                .setRoles(List.of(userRole)));
    }

    private List<OrderEntity> initTestOrders() {
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

        orderRepository.save(order1);
        orderRepository.save(order2);

        return List.of(order1, order2);
    }
}