package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.model.entity.*;
import bg.softuni.webbookstore.model.entity.enums.CategoryEnum;
import bg.softuni.webbookstore.model.entity.enums.LanguageEnum;
import bg.softuni.webbookstore.model.entity.enums.UserRoleEnum;
import bg.softuni.webbookstore.model.view.BookSummaryViewModel;
import bg.softuni.webbookstore.repository.*;
import bg.softuni.webbookstore.service.BookService;
import bg.softuni.webbookstore.service.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    private UserEntity testUser;
    private CategoryEntity testCategory;
    private PublishingHouseEntity testPublishingHouse;
    private AuthorEntity testAuthor;
    private List<BookEntity> testBooks;

    private BookService bookServiceToTest;

    @Mock
    private BookRepository mockBookRepository;

    @Mock
    private PictureRepository mockPictureRepository;

    @Mock
    private WishlistRepository mockWishlistRepository;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private CategoryRepository mockCategoryRepository;

    @Mock
    private PublishingHouseRepository mockPublishingHouseRepository;

    @Mock
    private AuthorRepository mockAuthorRepository;

    @Mock
    private CloudinaryService mockCloudinaryService;

    @Mock
    private ModelMapper mockModelMapper;

    @BeforeEach
    void setUp() {
        bookServiceToTest = new BookServiceImpl(mockBookRepository, mockPictureRepository,
                mockWishlistRepository, mockUserRepository,
                mockCategoryRepository, mockPublishingHouseRepository,
                mockAuthorRepository, mockCloudinaryService, mockModelMapper);

        testUser = initTestUser();
        testCategory = initCategory();
        testPublishingHouse = initPublishingHouse();
        testAuthor = initAuthor();
        testBooks = initBooks();
    }

    @Test
    void test_GetTopThreeNewestBooks_ReturnsCorrect() {
        Mockito.when(mockBookRepository.findTop3NewestByActiveTrueOrderByAddedOnDesc())
                .thenReturn(testBooks);

        Mockito.when(mockModelMapper.map(testBooks.get(0), BookSummaryViewModel.class))
                .thenReturn(new BookSummaryViewModel().setTitle(testBooks.get(0).getTitle()));
        Mockito.when(mockModelMapper.map(testBooks.get(1), BookSummaryViewModel.class))
                .thenReturn(new BookSummaryViewModel().setTitle(testBooks.get(1).getTitle()));
        Mockito.when(mockModelMapper.map(testBooks.get(2), BookSummaryViewModel.class))
                .thenReturn(new BookSummaryViewModel().setTitle(testBooks.get(2).getTitle()));

        String actualTitles = bookServiceToTest.findTopThreeNewestBooks()
                .stream()
                .map(BookSummaryViewModel::getTitle)
                .collect(Collectors.joining(", "));
        String expectedTitles = "TestBook1, TestBook2, TestBook3";

        assertEquals(expectedTitles, actualTitles);
    }


    private UserRoleEntity initUserRole() {
        return new UserRoleEntity()
                .setRole(UserRoleEnum.USER);
    }

    private UserRoleEntity initAdminRole() {
        return new UserRoleEntity()
                .setRole(UserRoleEnum.ADMIN);
    }

    private UserEntity initTestUser() {
        return new UserEntity()
                .setFirstName("Admin")
                .setLastName("Admin")
                .setUsername("adminuser")
                .setEmail("admin@admin.bg")
                .setAddress("Sofia")
                .setPassword("12345")
                .setRoles(List.of(initAdminRole()));
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

        BookEntity book3 = new BookEntity()
                .setIsbn("3")
                .setTitle("TestBook3")
                .setAddedOn(Instant.now())
                .setModified(Instant.now())
                .setActive(true)
                .setPagesCount(300)
                .setCopies(3)
                .setReleaseYear(2000)
                .setPrice(BigDecimal.valueOf(30))
                .setLanguage(LanguageEnum.BULGARIAN)
                .setCategories(List.of(testCategory))
                .setPublishingHouse(testPublishingHouse)
                .setAuthor(testAuthor)
                .setCreator(testUser);

        return List.of(book1, book2, book3);
    }

    private CategoryEntity initCategory() {
        return new CategoryEntity()
                .setCategory(CategoryEnum.FICTION);
    }

    private PublishingHouseEntity initPublishingHouse() {
        return new PublishingHouseEntity()
                .setName("TestPublishingHouse");
    }

    private AuthorEntity initAuthor() {
        return new AuthorEntity()
                .setFirstName("TestAuthor")
                .setLastName("TestAuthor");
    }
}