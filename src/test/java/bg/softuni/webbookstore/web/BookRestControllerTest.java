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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser("testUser")
@SpringBootTest
@AutoConfigureMockMvc
class BookRestControllerTest {

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

    private UserRoleEntity adminRole;
    private UserEntity testUser;
    private CategoryEntity testCategory;
    private PublishingHouseEntity testPublishingHouse;
    private AuthorEntity testAuthor;

    @BeforeEach
    void setUp() throws InterruptedException {
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        publishingHouseRepository.deleteAll();
        authorRepository.deleteAll();
        bookRepository.deleteAll();

        adminRole = initAdminRole();
        testUser = initTestUser();
        testCategory = initCategory();
        testPublishingHouse = initPublishingHouse();
        testAuthor = initAuthor();

        initBooks();
    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
        publishingHouseRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        userRoleRepository.deleteAll();
    }

    @Test
    void testGetBooks() throws Exception {
        mockMvc
                .perform(get("/books/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].title", is("TestBook2")))
                .andExpect(jsonPath("$.[1].title", is("TestBook1")));
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

    private void initBooks() throws InterruptedException {

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
                .setCreator(testUser);

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
                .setCreator(testUser);

        bookRepository.save(book1);
        Thread.sleep(1000);
        bookRepository.save(book2);
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