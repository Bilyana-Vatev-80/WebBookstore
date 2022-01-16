package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.model.entity.*;
import bg.softuni.webbookstore.model.entity.enums.CategoryEnum;
import bg.softuni.webbookstore.model.entity.enums.LanguageEnum;
import bg.softuni.webbookstore.model.entity.enums.UserRoleEnum;
import bg.softuni.webbookstore.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PublishingHouseRepository publishingHouseRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private UserRoleEntity adminRole;
    private UserEntity testUser;
    private CategoryEntity testCategory;
    private PublishingHouseEntity testPublishingHouse;
    private AuthorEntity testAuthor;
    private BookEntity testBook;

    @BeforeEach
    void setUp() throws InterruptedException {
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        publishingHouseRepository.deleteAll();
        authorRepository.deleteAll();
        cartItemRepository.deleteAll();
        bookRepository.deleteAll();

        adminRole = initAdminRole();
        testUser = initTestUser();
        testCategory = initCategory();
        testPublishingHouse = initPublishingHouse();
        testAuthor = initAuthor();
        testBook = initBook();
    }

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
        bookRepository.deleteAll();
        publishingHouseRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        userRoleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testUser")
    void test_GetShoppingCart_ReturnsCorrectly() throws Exception {
        mockMvc
                .perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("shopping-cart"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("itemsCounts"))
                .andExpect(model().attributeExists("totalPrice"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void test_AddToShoppingCartExistingItem_IncreasesQuantity() throws Exception {
        CartItemEntity testCartItem = cartItemRepository.save(new CartItemEntity()
                .setQuantity(1)
                .setBook(testBook)
                .setCustomer(testUser));

        mockMvc
                .perform(get("/cart/add/" + testBook.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        mockMvc
                .perform(get("/cart/add/" + testBook.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        assertEquals(1, cartItemRepository.count());

        CartItemEntity cartItem = cartItemRepository.findById(testCartItem.getId()).get();
        BookEntity book = bookRepository.findById(testBook.getId()).get();

        assertEquals(3, cartItem.getQuantity());
        assertEquals(0, book.getCopies());
    }

    @Test
    @WithMockUser(username = "testUser")
    void test_AddToShoppingCartNewItem_CreatesCartItem() throws Exception {
        mockMvc
                .perform(get("/cart/add/" + testBook.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        assertEquals(1, cartItemRepository.count());

        CartItemEntity cartItem = cartItemRepository
                .findByBookIdAndCustomerUsername(testBook.getId(), testUser.getUsername())
                .get();
        BookEntity book = bookRepository.findById(testBook.getId()).get();

        assertEquals(1, cartItem.getQuantity());
        assertEquals(1, book.getCopies());
    }

    @Test
    @WithMockUser(username = "testUser")
    void test_RemoveFromCart_ReturnsOrderedCopiesToBookAndRemovesCartItem() throws Exception {
        mockMvc
                .perform(get("/cart/add/" + testBook.getId()));

        assertEquals(1, cartItemRepository.count());

        BookEntity bookAfterAddToCart = bookRepository.findById(testBook.getId()).get();
        assertEquals(1, bookAfterAddToCart.getCopies());

        mockMvc
                .perform(get("/cart/remove/" + testBook.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        assertEquals(0, cartItemRepository.count());

        BookEntity bookAfterRemoveFromCart = bookRepository.findById(testBook.getId()).get();
        assertEquals(2, bookAfterRemoveFromCart.getCopies());
    }

    @Test
    @WithMockUser(username = "testUser")
    void test_DecreaseCartItemQuantity_ReturnsOneOrderedCopyToBookAndDecreasesCartItemQuantity() throws Exception {
        mockMvc
                .perform(get("/cart/add/" + testBook.getId()));
        mockMvc
                .perform(get("/cart/add/" + testBook.getId()));

        assertEquals(1, cartItemRepository.count());

        CartItemEntity cartItemAfterAddToCart = cartItemRepository
                .findByBookIdAndCustomerUsername(testBook.getId(), testUser.getUsername())
                .get();
        BookEntity bookAfterAddToCart = bookRepository.findById(testBook.getId()).get();

        assertEquals(2, cartItemAfterAddToCart.getQuantity());
        assertEquals(0, bookAfterAddToCart.getCopies());

        mockMvc
                .perform(get("/cart/decrease-quantity/" + testBook.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        assertEquals(1, cartItemRepository.count());

        CartItemEntity cartItemAfterDecrease = cartItemRepository
                .findByBookIdAndCustomerUsername(testBook.getId(), testUser.getUsername())
                .get();
        BookEntity bookAfterDecrease = bookRepository.findById(testBook.getId()).get();

        assertEquals(1, cartItemAfterDecrease.getQuantity());
        assertEquals(1, bookAfterDecrease.getCopies());
    }

    @Test
    @WithMockUser(username = "testUser")
    void test_GetCartConfirm_ReturnsCorrectly() throws Exception {
        mockMvc
                .perform(get("/cart/confirm"))
                .andExpect(status().isOk())
                .andExpect(view().name("order-confirm"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("itemsCounts"))
                .andExpect(model().attributeExists("totalPrice"));
    }

    private UserRoleEntity initAdminRole() {
        return userRoleRepository.save(new UserRoleEntity()
                .setRole(UserRoleEnum.ADMIN));
    }

    private UserEntity initTestUser() {
        return userRepository.save(new UserEntity()
                .setFirstName("Test")
                .setLastName("Testov")
                .setUsername("testUser")
                .setEmail("test@test.bg")
                .setAddress("Sofia")
                .setPassword("12345")
                .setRoles(List.of(adminRole)));
    }

    private BookEntity initBook() throws InterruptedException {
        return bookRepository.save(new BookEntity()
                .setIsbn("1")
                .setTitle("TestBook")
                .setAddedOn(Instant.now())
                .setModified(Instant.now())
                .setActive(true)
                .setPagesCount(100)
                .setCopies(2)
                .setReleaseYear(1000)
                .setPrice(BigDecimal.valueOf(10))
                .setLanguage(LanguageEnum.BULGARIAN)
                .setCategories(List.of(testCategory))
                .setPublishingHouse(testPublishingHouse)
                .setAuthor(testAuthor)
                .setCreator(testUser));
    }

    private CategoryEntity initCategory() {
        return categoryRepository.save(new CategoryEntity()
                .setCategory(CategoryEnum.FICTION));
    }

    private PublishingHouseEntity initPublishingHouse() {
        return publishingHouseRepository.save(new PublishingHouseEntity()
                .setName("TestPublishingHouse"));
    }

    private AuthorEntity initAuthor() {
        return authorRepository.save(new AuthorEntity()
                .setFirstName("TestAuthor")
                .setLastName("TestAuthor"));
    }
}