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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewRestControllerTest {

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
    private ReviewRepository reviewRepository;

    private UserRoleEntity adminRole;
    private UserEntity testUser1;
    private UserEntity testUser2;
    private CategoryEntity testCategory;
    private PublishingHouseEntity testPublishingHouse;
    private AuthorEntity testAuthor;
    private List<BookEntity> testBooks;
    private List<ReviewEntity> testReviews;

    @BeforeEach
    void setUp() throws Exception {
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        publishingHouseRepository.deleteAll();
        authorRepository.deleteAll();
        reviewRepository.deleteAll();
        bookRepository.deleteAll();

        adminRole = initAdminRole();
        testUser1 = initTestUserOne();
        testUser2 = initTestUserTwo();
        testCategory = initCategory();
        testPublishingHouse = initPublishingHouse();
        testAuthor = initAuthor();
        testBooks = initBooks();

        testReviews = initReviews();
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        publishingHouseRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        userRoleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testUser1", roles = "ADMIN")
    void test_GetReviewsByBook_ReturnsCorrectly() throws Exception {
        mockMvc
                .perform(get("/reviews/approve/" + testReviews.get(0).getId()));

        mockMvc
                .perform(get("/reviews/approve/" + testReviews.get(1).getId()));

        mockMvc
                .perform(get("/reviews/api/" + testBooks.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].rating", is(5)))
                .andExpect(jsonPath("$.[0].nickname", is("testUser1")))
                .andExpect(jsonPath("$.[0].bookTitle", is("TestBook1")));
    }

    @Test
    @WithMockUser(username = "testUser1", roles = "ADMIN")
    void test_GetReviewsByUser_ReturnsCorrectly() throws Exception {
        mockMvc
                .perform(get("/reviews/approve/" + testReviews.get(0).getId()));

        mockMvc
                .perform(get("/reviews/approve/" + testReviews.get(1).getId()));

        mockMvc
                .perform(get("/reviews/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].rating", is(5)))
                .andExpect(jsonPath("$.[0].nickname", is("testUser1")))
                .andExpect(jsonPath("$.[0].bookTitle", is("TestBook1")));
    }

    @Test
    void test_GetReviewsByUserWithNoPrincipal_RedirectsToLogin() throws Exception {
        mockMvc
                .perform(get("/reviews/api/user"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().stringValues("Location", "/users/login"));
    }

    private UserRoleEntity initAdminRole() {
        return userRoleRepository.save(new UserRoleEntity()
                .setRole(UserRoleEnum.ADMIN));
    }

    private UserEntity initTestUserOne() {
        return userRepository.save(new UserEntity()
                .setFirstName("Test")
                .setLastName("Testov")
                .setUsername("testUser1")
                .setEmail("testOne@test.bg")
                .setAddress("Sofia")
                .setPassword("12345")
                .setRoles(List.of(adminRole)));
    }

    private UserEntity initTestUserTwo() {
        return userRepository.save(new UserEntity()
                .setFirstName("Test")
                .setLastName("Testov")
                .setUsername("testUser2")
                .setEmail("testTwo@test.bg")
                .setAddress("Sofia")
                .setPassword("12345")
                .setRoles(List.of(adminRole)));
    }

    private List<BookEntity> initBooks() throws InterruptedException {

        BookEntity book1 = new BookEntity()
                .setIsbn("1")
                .setTitle("TestBook1")
                .setAddedOn(Instant.now())
                .setModified(Instant.now())
                .setActive(true)
                .setPagesCount(100)
                .setCopies(1)
                .setReleaseYear(1000)
                .setPrice(BigDecimal.valueOf(10))
                .setLanguage(LanguageEnum.BULGARIAN)
                .setCategories(List.of(testCategory))
                .setPublishingHouse(testPublishingHouse)
                .setAuthor(testAuthor)
                .setCreator(testUser1);

        BookEntity book2 = new BookEntity()
                .setIsbn("2")
                .setTitle("TestBook2")
                .setAddedOn(Instant.now())
                .setModified(Instant.now())
                .setActive(true)
                .setPagesCount(200)
                .setCopies(2)
                .setReleaseYear(2000)
                .setPrice(BigDecimal.valueOf(20))
                .setLanguage(LanguageEnum.BULGARIAN)
                .setCategories(List.of(testCategory))
                .setPublishingHouse(testPublishingHouse)
                .setAuthor(testAuthor)
                .setCreator(testUser1);

        bookRepository.save(book1);
        Thread.sleep(1000);
        bookRepository.save(book2);

        return List.of(book1, book2);
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

    private List<ReviewEntity> initReviews() {
        ReviewEntity review1 = reviewRepository.save(new ReviewEntity()
                .setNickname("testUser1")
                .setRating(5)
                .setAddedOn(Instant.now())
                .setAuthor(testUser1)
                .setBook(testBooks.get(0)));

        ReviewEntity review2 = reviewRepository.save(new ReviewEntity()
                .setNickname("testUser2")
                .setRating(5)
                .setAddedOn(Instant.now())
                .setAuthor(testUser2)
                .setBook(testBooks.get(1)));

        return List.of(review1, review2);
    }
}