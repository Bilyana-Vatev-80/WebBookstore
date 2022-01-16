package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.model.entity.BookEntity;
import bg.softuni.webbookstore.model.entity.ReviewEntity;
import bg.softuni.webbookstore.model.entity.UserEntity;
import bg.softuni.webbookstore.model.service.ReviewAddServiceModel;
import bg.softuni.webbookstore.model.view.ReviewViewModel;
import bg.softuni.webbookstore.repository.BookRepository;
import bg.softuni.webbookstore.repository.ReviewRepository;
import bg.softuni.webbookstore.repository.UserRepository;
import bg.softuni.webbookstore.service.ReviewService;
import bg.softuni.webbookstore.web.exception.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static bg.softuni.webbookstore.constant.GlobalConstants.*;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, BookRepository bookRepository, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ReviewViewModel> findAllReviewsByBook(Long bookId) {
        return reviewRepository
                .findAllByApprovedTrueAndBookId(bookId)
                .stream()
                .map(this::getReviewViewModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewViewModel> findAllReviewsByUser(String username) {
        return reviewRepository
                .findAllByApprovedTrueAndAuthorUsername(username)
                .stream()
                .map(this::getReviewViewModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewViewModel> findAllNotApprovedReviews() {
        return reviewRepository
                .findAllByApprovedFalse()
                .stream()
                .map(this::getReviewViewModel)
                .collect(Collectors.toList());
    }

    @Override
    public Long add(ReviewAddServiceModel serviceModel) {
        ReviewEntity reviewEntity = modelMapper
                .map(serviceModel, ReviewEntity.class)
                .setAuthor(getUserEntity(serviceModel.getAuthor()))
                .setBook(getBookEntity(serviceModel.getBookNum()));

        ReviewEntity newEntity = reviewRepository.save(reviewEntity);

        return newEntity.getId();
    }

//    @Override
//    public void delete(Long id) {
//        reviewRepository.deleteById(id);
//    }

    @Override
    public void deleteAllReviewsForBook(Long bookId) {
        reviewRepository.deleteAllByBookId(bookId);
    }

    @Override
    public void deleteReviewsOlderThanOneYear() {
        Instant previousYear = Instant.now().minus(365, ChronoUnit.DAYS);
        reviewRepository.deleteAllByAddedOnBefore(previousYear);
    }

    @Override
    public void approve(Long id) {
        ReviewEntity reviewEntity = reviewRepository
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_REVIEW));

        reviewEntity.setApproved(true);
        reviewRepository.save(reviewEntity);
    }


    private ReviewViewModel getReviewViewModel(ReviewEntity reviewEntity) {
        return modelMapper
                .map(reviewEntity, ReviewViewModel.class)
                .setAddedOn(reviewEntity.getAddedOn().atZone(ZoneId.systemDefault()));
    }

    private UserEntity getUserEntity(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_USER));
    }

    private BookEntity getBookEntity(Long id) {
        return bookRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_BOOK));
    }
}
