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
import org.springframework.http.HttpMethod;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    private static final String TEST_BOOK_TO_ADD_ISBN = "123456789";
    private static final String TEST_BOOK_TO_ADD_TITLE = "Test Book To Add";
    private static final int TEST_BOOK_TO_ADD_PAGES_COUNT = 555;
    private static final int TEST_BOOK_TO_ADD_COPIES = 5;
    private static final int TEST_BOOK_TO_ADD_RELEASE_YEAR = 2000;
    private static final BigDecimal TEST_BOOK_TO_ADD_PRICE = BigDecimal.valueOf(20.0);
    private static final String TEST_BOOK_TO_ADD_LANGUAGE = "Bulgarian";

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
    private List<BookEntity> testBooks;

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
        testAuthor = initAuthor();
        testBooks = initBooks();
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
    void test_GetAllBooks_ReturnsCorrectly() throws Exception {
        mockMvc
                .perform(get("/books/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("all-books"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void test_GetAddBookForm_OpensForm() throws Exception {
        mockMvc
                .perform(get("/books/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-book"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    void test_GetAddBookFormWithNoAdmin_ReturnsUnauthorized() throws Exception {
        mockMvc
                .perform(get("/books/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/unauthorized"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void test_AddBook_SavesNewBookCorrectly() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "", "text/plain", "some_xml".getBytes());

        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.multipart("/books/add")
                        .file("image", image.getBytes())
                        .param("isbn", TEST_BOOK_TO_ADD_ISBN)
                        .param("title", TEST_BOOK_TO_ADD_TITLE)
                        .param("pagesCount", String.valueOf(TEST_BOOK_TO_ADD_PAGES_COUNT))
                        .param("copies", String.valueOf(TEST_BOOK_TO_ADD_COPIES))
                        .param("releaseYear", String.valueOf(TEST_BOOK_TO_ADD_RELEASE_YEAR))
                        .param("price", String.valueOf(TEST_BOOK_TO_ADD_PRICE))
                        .param("language", TEST_BOOK_TO_ADD_LANGUAGE)
                        .param("categories", testCategory.getCategory().toString())
                        .param("publishingHouse", testPublishingHouse.getName())
                        .param("authorFirstName", testAuthor.getFirstName())
                        .param("authorLastName", testAuthor.getLastName())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                );

        Optional<BookEntity> newlyCreatedBookOpt = bookRepository.findByIsbn(TEST_BOOK_TO_ADD_ISBN);
        assertTrue(newlyCreatedBookOpt.isPresent());

        BookEntity newlyCreatedBook = newlyCreatedBookOpt.get();
        assertEquals(TEST_BOOK_TO_ADD_TITLE, newlyCreatedBook.getTitle());
        assertEquals(TEST_BOOK_TO_ADD_PAGES_COUNT, newlyCreatedBook.getPagesCount());
        assertEquals(TEST_BOOK_TO_ADD_COPIES, newlyCreatedBook.getCopies());
        assertEquals(TEST_BOOK_TO_ADD_RELEASE_YEAR, newlyCreatedBook.getReleaseYear());
        assertEquals(TEST_BOOK_TO_ADD_LANGUAGE, newlyCreatedBook.getLanguage().toString());

        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + newlyCreatedBook.getId()));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void test_AddBookInvalidParams_RedirectsToAddForm() throws Exception {
        mockMvc
                .perform(post("/books/add")
                        .param("isbn", TEST_BOOK_TO_ADD_ISBN)
                        .param("title", TEST_BOOK_TO_ADD_TITLE)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/add"));
    }

    @Test
    void test_GetBookDetails_ReturnsCorrectly() throws Exception {
        BookEntity bookToSearch = testBooks.get(0);
        mockMvc
                .perform(get("/books/" + bookToSearch.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("book-details"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    void test_GetBookDetailsInvalidId_Throws() throws Exception {
        mockMvc
                .perform(get("/books/100000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void test_GetUpdateBookForm_OpensForm() throws Exception {
        BookEntity bookToEdit = testBooks.get(0);

        mockMvc
                .perform(get("/books/edit/" + bookToEdit.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-book"))
                .andExpect(model().attributeExists("languages"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("publishingHouses"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void test_EditBook_SavesUpdatedBookCorrectly() throws Exception {
        BookEntity bookToEdit = testBooks.get(0);

        MockMultipartFile image = new MockMultipartFile("image", "", "text/plain", "some_xml".getBytes());

        ResultActions resultActions = mockMvc
                .perform(MockMvcRequestBuilders.multipart("/books/edit/" + bookToEdit.getId())
                        .file("image", image.getBytes())
                        .with(request -> {
                            request.setMethod(String.valueOf(HttpMethod.PATCH));
                            return request;
                        })
                        .param("isbn", bookToEdit.getIsbn())
                        .param("title", bookToEdit.getTitle())
                        .param("pagesCount", String.valueOf(TEST_BOOK_TO_ADD_PAGES_COUNT))
                        .param("copies", String.valueOf(TEST_BOOK_TO_ADD_COPIES))
                        .param("releaseYear", String.valueOf(bookToEdit.getReleaseYear()))
                        .param("price", String.valueOf(bookToEdit.getPrice()))
                        .param("language", bookToEdit.getLanguage().toString())
                        .param("categories", testCategory.getCategory().toString())
                        .param("publishingHouseName", testPublishingHouse.getName())
                        .param("authorFirstName", testAuthor.getFirstName())
                        .param("authorLastName", testAuthor.getLastName())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                );

        Optional<BookEntity> updatedBookOpt = bookRepository.findByIdAndActiveTrue(bookToEdit.getId());
        assertTrue(updatedBookOpt.isPresent());

        BookEntity updatedBook = updatedBookOpt.get();
        assertEquals(TEST_BOOK_TO_ADD_PAGES_COUNT, updatedBook.getPagesCount());
        assertEquals(TEST_BOOK_TO_ADD_COPIES, updatedBook.getCopies());

        assertEquals(testBooks.size(), bookRepository.count());

        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + updatedBook.getId()));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void test_EditBookWithInvalidParams_RedirectsToEditErrorsPage() throws Exception {
        BookEntity bookToEdit = testBooks.get(0);

        mockMvc
                .perform(patch("/books/edit/" + bookToEdit.getId())
                        .param("pagesCount", String.valueOf(TEST_BOOK_TO_ADD_PAGES_COUNT))
                        .param("copies", String.valueOf(TEST_BOOK_TO_ADD_COPIES))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/edit/" + bookToEdit.getId() + "/errors"));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "ADMIN")
    void test_Delete_WorksCorrectly() throws Exception {
        BookEntity bookToDelete = testBooks.get(0);
        mockMvc
                .perform(delete("/books/" + bookToDelete.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/all"));

        BookEntity deletedBook = bookRepository.findById(bookToDelete.getId()).get();
        assertFalse(deletedBook.getActive());
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

    private List<BookEntity> initBooks() {

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
}