package bg.softuni.webbookstore.service.scheduler;

import bg.softuni.webbookstore.service.ReviewService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RemoveOldReviewsScheduler {

    private final ReviewService reviewService;

    public RemoveOldReviewsScheduler(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Scheduled(cron = "${schedulers.cron}")
    public void cleanUpOldReviews() {
        reviewService.deleteReviewsOlderThanOneYear();
    }
}
