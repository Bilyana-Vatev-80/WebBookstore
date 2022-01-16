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
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
class PublishingHouseControllerTest {

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
    private AuthorEntity testAuthor;
    private PublishingHouseEntity testPublishingHouse1;
    private PublishingHouseEntity testPublishingHouse2;

    @BeforeEach
    void setUp() {
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        publishingHouseRepository.deleteAll();
        authorRepository.deleteAll();
        bookRepository.deleteAll();

        adminRole = initAdminRole();
        testUser = initTestUser();
        testCategory = initCategory();
        testAuthor = initAuthor();
        testPublishingHouse1 = initPublishingHouseOne();
        testPublishingHouse2 = initPublishingHouseTwo();

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
    void test_GetPublishingHouseDetails_ReturnsCorrect() throws Exception {
        mockMvc
                .perform(get("/publishinghouses/" + testPublishingHouse1.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("publishing-house-details"))
                .andExpect(model().attributeExists("publishingHouse"))
                .andExpect(model().attributeExists("books"));

        String actualBookTitles = bookRepository
                .findAllByActiveTrueAndPublishingHouseIdOrderByAddedOnDesc(testPublishingHouse1.getId())
                .stream()
                .map(BookEntity::getTitle)
                .collect(Collectors.joining());
        String expectedBookTitles = "TestBook1";
        assertEquals(expectedBookTitles, actualBookTitles);
    }

    @Test
    void test_GetPublishingHouseDetailsInvalidId_Throws() throws Exception {
        mockMvc
                .perform(get("/publishinghouses/100000000000000"))
                .andExpect(status().isNotFound());
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

    private void initBooks() {

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
                .setPublishingHouse(testPublishingHouse1)
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
                .setPublishingHouse(testPublishingHouse2)
                .setAuthor(testAuthor)
                .setCreator(testUser);

        bookRepository.save(book1);
        bookRepository.save(book2);
    }

    private CategoryEntity initCategory() {
        return categoryRepository.save(new CategoryEntity()
                .setCategory(CategoryEnum.FICTION));
    }

    private AuthorEntity initAuthor() {
        return authorRepository.save(new AuthorEntity()
                .setFirstName("TestAuthor")
                .setLastName("TestAuthor"));
    }

    private PublishingHouseEntity initPublishingHouseOne() {
        return publishingHouseRepository.save(new PublishingHouseEntity()
                .setName("TestPublishingHouse1"));
    }

    private PublishingHouseEntity initPublishingHouseTwo() {
        return publishingHouseRepository.save(new PublishingHouseEntity()
                .setName("TestPublishingHouse2"));
    }
}