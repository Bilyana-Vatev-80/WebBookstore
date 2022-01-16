package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.model.binding.BookUpdateBindingModel;
import bg.softuni.webbookstore.model.cloudinary.CloudinaryImage;
import bg.softuni.webbookstore.model.entity.*;
import bg.softuni.webbookstore.model.entity.enums.CategoryEnum;
import bg.softuni.webbookstore.model.entity.enums.LanguageEnum;
import bg.softuni.webbookstore.model.service.BookAddServiceModel;
import bg.softuni.webbookstore.model.service.BookUpdateServiceModel;
import bg.softuni.webbookstore.model.view.BookDetailViewModel;
import bg.softuni.webbookstore.model.view.BookSummaryViewModel;
import bg.softuni.webbookstore.repository.*;
import bg.softuni.webbookstore.service.*;
import bg.softuni.webbookstore.utils.StringUtils;
import bg.softuni.webbookstore.web.exception.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static bg.softuni.webbookstore.constant.GlobalConstants.*;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final PictureRepository pictureRepository;
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PublishingHouseRepository publishingHouseRepository;
    private final AuthorRepository authorRepository;
    private final CloudinaryService cloudinaryService;
    private final ModelMapper modelMapper;

    public BookServiceImpl(BookRepository bookRepository, PictureRepository pictureRepository, WishlistRepository wishlistRepository, UserRepository userRepository, CategoryRepository categoryRepository, PublishingHouseRepository publishingHouseRepository, AuthorRepository authorRepository, CloudinaryService cloudinaryService, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.pictureRepository = pictureRepository;
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.publishingHouseRepository = publishingHouseRepository;
        this.authorRepository = authorRepository;
        this.cloudinaryService = cloudinaryService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public List<BookSummaryViewModel> findAllBooks() {
        return bookRepository
                .findAllByActiveTrueOrderByAddedOnDesc()
                .stream()
                .map(this::getSummaryViewModel)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookSummaryViewModel> findAllBooksOrderByTitle() {
        return bookRepository
                .findAllByActiveTrueOrderByTitleAscAddedOnDesc()
                .stream()
                .map(this::getSummaryViewModel)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookSummaryViewModel> findAllBooksOrderByAuthor() {
        return bookRepository
                .findAllByActiveTrueOrderByAuthor()
                .stream()
                .map(this::getSummaryViewModel)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookSummaryViewModel> findAllBooksOrderByBestSelling() {
        return bookRepository
                .findAllByActiveTrueOrderByBestSelling()
                .stream()
                .map(this::getSummaryViewModel)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookSummaryViewModel> findTopThreeNewestBooks() {
        return bookRepository
                .findTop3NewestByActiveTrueOrderByAddedOnDesc()
                .stream()
                .map(this::getSummaryViewModel)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookSummaryViewModel> findTopThreeMostPopularBooks() {
        return bookRepository
                .findTop3MostPopularBySoldCopiesAndReviewsCount(PageRequest.of(0, 3))
                .stream()
                .map(this::getSummaryViewModel)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookSummaryViewModel> findBooksByAuthor(Long id) {
        return bookRepository
                .findAllByActiveTrueAndAuthorIdOrderByAddedOnDesc(id)
                .stream()
                .map(this::getSummaryViewModel)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookSummaryViewModel> findBooksByPublishingHouse(Long id) {
        return bookRepository
                .findAllByActiveTrueAndPublishingHouseIdOrderByAddedOnDesc(id)
                .stream()
                .map(this::getSummaryViewModel)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookSummaryViewModel> getWishListBooksByCustomer(String username) {
        return wishlistRepository
                .findAllByCustomerUsername(username)
                .stream()
                .map(wishlistItemEntity ->
                        getSummaryViewModel(wishlistItemEntity.getBook()))
                .collect(Collectors.toList());
    }

    @Override
    public String findBookTitleById(Long id) {
        return bookRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ObjectNotFoundException(OBJECT_NAME_BOOK))
                .getTitle();
    }

    @Override
    public Long add(BookAddServiceModel bookAddServiceModel) throws IOException {

        MultipartFile img = bookAddServiceModel.getImage();

        BookEntity bookEntity = modelMapper
                .map(bookAddServiceModel, BookEntity.class)
                .setActive(true)
                .setPicture(getPictureEntity(img))
                .setLanguage(getLanguageEnum(bookAddServiceModel.getLanguage()))
                .setCategories(getCategoryEntities(bookAddServiceModel.getCategories()))
                .setPublishingHouse(getPublishingHouseEntity(bookAddServiceModel.getPublishingHouse()))
                .setAuthor(getAuthorEntity(
                        bookAddServiceModel.getAuthorFirstName(),
                        bookAddServiceModel.getAuthorLastName()))
                .setCreator(getUserEntity(bookAddServiceModel.getCreator()));

        bookEntity = bookRepository.save(bookEntity);

        return bookEntity.getId();
    }

    @Transactional
    @Override
    public Optional<BookDetailViewModel> findBookDetails(Long id) {
        return bookRepository
                .findByIdAndActiveTrue(id)
                .map(this::getBookDetailViewModel);
    }

    @Transactional
    @Override
    public Optional<BookUpdateBindingModel> findBookToEdit(Long id) {
        return bookRepository
                .findByIdAndActiveTrue(id)
                .map(this::getBookToEdit);
    }

    @Override
    public Long update(BookUpdateServiceModel bookUpdateServiceModel) throws IOException {
        BookEntity bookEntity = bookRepository
                .findByIdAndActiveTrue(bookUpdateServiceModel.getId())
                .orElseThrow(() ->
                        new ObjectNotFoundException(OBJECT_NAME_BOOK));

        MultipartFile img = bookUpdateServiceModel.getImage();
        if (!"".equals(img.getOriginalFilename())) {
            deleteOldPicture(bookEntity.getId());
            bookEntity.setPicture(getPictureEntity(img));
        }

        bookEntity
                .setIsbn(bookUpdateServiceModel.getIsbn())
                .setDescription(bookUpdateServiceModel.getDescription())
                .setPagesCount(bookUpdateServiceModel.getPagesCount())
                .setCopies(bookUpdateServiceModel.getCopies())
                .setReleaseYear(bookUpdateServiceModel.getReleaseYear())
                .setPrice(bookUpdateServiceModel.getPrice())
                .setLanguage(getLanguageEnum(bookUpdateServiceModel.getLanguage()))
                .setCategories(getCategoryEntities(bookUpdateServiceModel.getCategories()))
                .setPublishingHouse(getPublishingHouseEntity(bookUpdateServiceModel.getPublishingHouseName()))
                .setAuthor(getAuthorEntity(
                        bookUpdateServiceModel.getAuthorFirstName(),
                        bookUpdateServiceModel.getAuthorLastName()));

        bookRepository.save(bookEntity);

        return bookUpdateServiceModel.getId();
    }

    @Override
    public void delete(Long id) {
        BookEntity bookEntity = bookRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ObjectNotFoundException(OBJECT_NAME_BOOK));
        bookEntity.setActive(false);
        bookRepository.save(bookEntity);
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    @Override
    public void increaseWithOneCopy(Long id) {
        BookEntity bookEntity = bookRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ObjectNotFoundException(OBJECT_NAME_BOOK));

        bookEntity.setCopies(bookEntity.getCopies() + 1);
        bookRepository.save(bookEntity);
    }

    @Override
    public void decreaseWithOneCopy(Long id) {
        BookEntity bookEntity = bookRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ObjectNotFoundException(OBJECT_NAME_BOOK));

        bookEntity.setCopies(bookEntity.getCopies() - 1);
        bookRepository.save(bookEntity);
    }

    @Override
    public List<String> findAllBookTitlesWithTwoOrLessCopies() {
        return bookRepository.findAllBookTitlesWithTwoOrLessCopies();
    }

    @Override
    public Map<String, Integer> getBookCategoriesMap() {
        Map<String, Integer> categoriesMap = new HashMap<>();

        for (CategoryEnum categoryEnum : CategoryEnum.values()) {
            CategoryEntity category = categoryRepository
                    .findByCategory(categoryEnum)
                    .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_CATEGORY));
            categoriesMap.put(categoryEnum.name(), bookRepository.findBooksCountByCategory(category));
        }

        return categoriesMap;
    }

    private BookSummaryViewModel getSummaryViewModel(BookEntity bookEntity) {
        return modelMapper
                .map(bookEntity, BookSummaryViewModel.class)
                .setCategories(StringUtils.getCategoriesAsStrings(bookEntity.getCategories()))
                .setAuthor(StringUtils.getFullNameAsString(
                        bookEntity.getAuthor().getFirstName(),
                        bookEntity.getAuthor().getLastName()))
                .setAuthorId(bookEntity.getAuthor().getId());
    }

    private BookDetailViewModel getBookDetailViewModel(BookEntity bookEntity) {
        return modelMapper
                .map(bookEntity, BookDetailViewModel.class)
                .setAddedOn(bookEntity.getAddedOn().atZone(ZoneId.systemDefault()))
                .setModified(bookEntity.getModified().atZone(ZoneId.systemDefault()))
                .setCategories(StringUtils.getCategoriesAsStrings(bookEntity.getCategories()))
                .setAuthor(StringUtils.getFullNameAsString(
                        bookEntity.getAuthor().getFirstName(),
                        bookEntity.getAuthor().getLastName()))
                .setAuthorId(bookEntity.getAuthor().getId())
                .setCreator(StringUtils.getFullNameAsString(
                        bookEntity.getCreator().getFirstName(),
                        bookEntity.getCreator().getLastName()));
    }

    private BookUpdateBindingModel getBookToEdit(BookEntity bookEntity) {
        return modelMapper
                .map(bookEntity, BookUpdateBindingModel.class)
                .setCategories(StringUtils.getCategoriesAsStrings(bookEntity.getCategories()));
    }

    private UserEntity getUserEntity(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_USER));
    }

    private AuthorEntity getAuthorEntity(String firstName, String lastName) {
        return authorRepository
                .findByFirstNameAndLastName(firstName, lastName)
                .orElseGet(() -> {
                    AuthorEntity newAuthor = new AuthorEntity()
                            .setFirstName(firstName)
                            .setLastName(lastName)
                            .setPicture(pictureRepository.save(new PictureEntity(DEFAULT_AUTHOR_IMAGE_URL)));
                    return authorRepository.save(newAuthor);
                });
    }

    private PublishingHouseEntity getPublishingHouseEntity(String publishingHouseName) {
        return publishingHouseRepository
                .findByName(publishingHouseName)
                .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_PUBLISHING_HOUSE));
    }

    private LanguageEnum getLanguageEnum(String language) {
        return LanguageEnum.valueOf(language.toUpperCase());
    }

    private List<CategoryEntity> getCategoryEntities(List<String> categories) {
        return categories
                .stream()
                .map(category -> {
                    CategoryEnum categoryEnum = CategoryEnum.valueOf(
                            category.toUpperCase().replaceAll(" ", "_"));

                    return categoryRepository
                            .findByCategory(categoryEnum)
                            .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_CATEGORY));
                })
                .collect(Collectors.toList());
    }

    private PictureEntity getPictureEntity(MultipartFile img) throws IOException {
        if (!"".equals(img.getOriginalFilename())) {
            final CloudinaryImage uploaded = cloudinaryService.uploadImage(img);
            return pictureRepository.save(new PictureEntity()
                    .setUrl(uploaded.getUrl())
                    .setPublicId(uploaded.getPublicId()));
        } else {
            return pictureRepository.save(new PictureEntity(DEFAULT_BOOK_IMAGE_URL));
        }
    }

    private void deleteOldPicture(Long bookId) {
        BookEntity bookEntity = bookRepository
                .findByIdAndActiveTrue(bookId)
                .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_BOOK));

        if (bookEntity.getPicture().getPublicId() != null) {
            cloudinaryService.deleteImage(bookEntity.getPicture().getPublicId());
        }
        Long pictureId = bookEntity.getPicture().getId();
        bookEntity.setPicture(null);
        bookRepository.save(bookEntity);
        if (!bookRepository.existsByPictureId(pictureId)) {
            pictureRepository.deleteById(pictureId);
        }
    }
}
