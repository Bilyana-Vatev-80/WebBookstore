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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest {

    private static final String TEST_AUTHOR_TO_ADD_FIRST_NAME = "TestAuthor";
    private static final String TEST_AUTHOR_TO_ADD_LAST_NAME = "TestAuthor";

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
    private AuthorEntity testAuthor1;
    private AuthorEntity testAuthor2;

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
        testPublishingHouse = initPublishingHouse();
        testAuthor1 = initAuthorOne();
        testAuthor2 = initAuthorTwo();

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
    @WithMockUser(roles = "ADMIN")
    void test_GetAddAuthorForm_OpensForm() throws Exception {
        mockMvc
                .perform(get("/authors/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-author"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void test_AddAuthor_SavesNewAuthorCorrectly() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "", "text/plain", "some_xml".getBytes());

        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.multipart("/authors/add")
                        .file("image", image.getBytes())
                        .param("firstName", TEST_AUTHOR_TO_ADD_FIRST_NAME)
                        .param("lastName", TEST_AUTHOR_TO_ADD_LAST_NAME)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                );

        Optional<AuthorEntity> newlyCreatedAuthorOpt = authorRepository
                .findByFirstNameAndLastName(TEST_AUTHOR_TO_ADD_FIRST_NAME, TEST_AUTHOR_TO_ADD_LAST_NAME);
        assertTrue(newlyCreatedAuthorOpt.isPresent());

        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authors/" + newlyCreatedAuthorOpt.get().getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void test_AddAuthorWithInvalidParams_ReturnsToAddForm() throws Exception {
        mockMvc
                .perform(post("/authors/add")
                        .param("firstName", "")
                        .param("lastName", "")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authors/add"));
    }

    @Test
    void test_GetAuthorDetails_ReturnsCorrect() throws Exception {
        mockMvc
                .perform(get("/authors/" + testAuthor1.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("author-details"))
                .andExpect(model().attributeExists("author"))
                .andExpect(model().attributeExists("books"));

        String actualBookTitles = bookRepository
                .findAllByActiveTrueAndAuthorIdOrderByAddedOnDesc(testAuthor1.getId())
                .stream()
                .map(BookEntity::getTitle)
                .collect(Collectors.joining());
        String expectedBookTitles = "TestBook1";
        assertEquals(expectedBookTitles, actualBookTitles);
    }

    @Test
    void test_GetAuthorDetailsInvalidId_Throws() throws Exception {
        mockMvc
                .perform(get("/authors/100000000000000"))
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
                .setPublishingHouse(testPublishingHouse)
                .setAuthor(testAuthor1)
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
                .setAuthor(testAuthor2)
                .setCreator(testUser);

        bookRepository.save(book1);
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

    private AuthorEntity initAuthorOne() {
        return authorRepository.save(new AuthorEntity()
                .setFirstName("TestAuthor1")
                .setLastName("TestAuthor1"));
    }

    private AuthorEntity initAuthorTwo() {
        return authorRepository.save(new AuthorEntity()
                .setFirstName("TestAuthor2")
                .setLastName("TestAuthor2"));
    }
}