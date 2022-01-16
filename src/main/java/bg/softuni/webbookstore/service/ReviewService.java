package bg.softuni.webbookstore.service;

import bg.softuni.webbookstore.model.service.ReviewAddServiceModel;
import bg.softuni.webbookstore.model.view.ReviewViewModel;

import java.util.List;

public interface ReviewService {

    List<ReviewViewModel> findAllReviewsByBook(Long bookId);

    List<ReviewViewModel> findAllReviewsByUser(String username);

    List<ReviewViewModel> findAllNotApprovedReviews();

    Long add(ReviewAddServiceModel serviceModel);

//    void delete(Long id);

    void deleteAllReviewsForBook(Long bookId);

    void deleteReviewsOlderThanOneYear();

    void approve(Long id);
}
