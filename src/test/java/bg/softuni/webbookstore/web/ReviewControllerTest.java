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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewControllerTest {

    private static final String TEST_REVIEW_TO_ADD_NICKNAME = "Test";
    private static final int TEST_REVIEW_TO_ADD_RATING = 5;

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
        reviewRepository.deleteAll();
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
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        publishingHouseRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        userRoleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void test_GetNotApprovedReviews_ReturnsNewlyAddedReviews() throws Exception {
        mockMvc
                .perform(post("/reviews/add")
                        .param("bookId", String.valueOf(testBook.getId()))
                        .param("nickname", TEST_REVIEW_TO_ADD_NICKNAME)
                        .param("rating", String.valueOf(TEST_REVIEW_TO_ADD_RATING))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                );

        mockMvc
                .perform(post("/reviews/add")
                        .param("bookId", String.valueOf(testBook.getId()))
                        .param("nickname", TEST_REVIEW_TO_ADD_NICKNAME)
                        .param("rating", String.valueOf(TEST_REVIEW_TO_ADD_RATING))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                );

        mockMvc
                .perform(get("/reviews/not-approved"))
                .andExpect(status().isOk())
                .andExpect(view().name("not-approved-reviews"))
                .andExpect(model().attributeExists("reviews"));

        assertEquals(2, reviewRepository.count());
        Optional<ReviewEntity> approvedReview = reviewRepository
                .findAll()
                .stream()
                .filter(ReviewEntity::getApproved)
                .findAny();

        assertTrue(approvedReview.isEmpty());
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void test_GetAddReviewForm_OpensForm() throws Exception {
        mockMvc
                .perform(get("/reviews/add/" + testBook.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-review"))
                .andExpect(model().attributeExists("bookTitle"))
                .andExpect(model().attributeExists("bookId"));
    }

    @Test
    void test_GetAddReviewWithNoLoggedUser_RedirectsToLogin() throws Exception {
        mockMvc
                .perform(get("/reviews/add/" + testBook.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/users/login"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void test_AddReview_SavesNewReviewCorrectly() throws Exception {
        mockMvc
                .perform(post("/reviews/add")
                        .param("bookId", String.valueOf(testBook.getId()))
                        .param("nickname", TEST_REVIEW_TO_ADD_NICKNAME)
                        .param("rating", String.valueOf(TEST_REVIEW_TO_ADD_RATING))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + testBook.getId()));

        assertEquals(1, reviewRepository.count());
        Optional<ReviewEntity> savedReviewOpt = reviewRepository
                .findAllByApprovedFalse()
                .stream()
                .findFirst();
        assertTrue(savedReviewOpt.isPresent());

        ReviewEntity savedReview = savedReviewOpt.get();
        assertEquals(testBook.getId(), savedReview.getBook().getId());
        assertEquals(TEST_REVIEW_TO_ADD_NICKNAME, savedReview.getNickname());
        assertEquals(TEST_REVIEW_TO_ADD_RATING, savedReview.getRating());
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void test_AddReviewInvalidParams_RedirectsToAddForm() throws Exception {
        mockMvc
                .perform(post("/reviews/add")
                        .param("bookId", String.valueOf(testBook.getId()))
                        .param("nickname", TEST_REVIEW_TO_ADD_NICKNAME)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reviews/add/" + testBook.getId()));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void test_ApproveReview_ChangesReviewApprovedStatus() throws Exception {
        mockMvc
                .perform(post("/reviews/add")
                        .param("bookId", String.valueOf(testBook.getId()))
                        .param("nickname", TEST_REVIEW_TO_ADD_NICKNAME)
                        .param("rating", String.valueOf(TEST_REVIEW_TO_ADD_RATING))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                );

        ReviewEntity savedReview = reviewRepository
                .findAllByApprovedFalse()
                .stream()
                .findFirst()
                .get();

        mockMvc
                .perform(get("/reviews/approve/" + savedReview.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reviews/not-approved"));

        ReviewEntity approvedReview = reviewRepository
                .findAll()
                .stream()
                .findFirst()
                .get();

        assertTrue(approvedReview.getApproved());
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
                .setCopies(1)
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