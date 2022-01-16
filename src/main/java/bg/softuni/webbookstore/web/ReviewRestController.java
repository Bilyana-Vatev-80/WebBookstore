package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.model.view.ReviewViewModel;
import bg.softuni.webbookstore.service.ReviewService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewRestController {

    private final ReviewService reviewService;

    public ReviewRestController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/api/{bookId}")
    public ResponseEntity<List<ReviewViewModel>> getAllReviewsByBook(@PathVariable Long bookId) {

        List<ReviewViewModel> reviewViewModels = reviewService.findAllReviewsByBook(bookId);

        return ResponseEntity
                .ok()
                .body(reviewViewModels);
    }

    @GetMapping("/api/user")
    public ResponseEntity<List<ReviewViewModel>> getAllReviewsByUser(
            @AuthenticationPrincipal UserDetails principal) throws IOException {

        if (principal == null) {
            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .header(HttpHeaders.LOCATION, "/users/login")
                    .build();
        }

        List<ReviewViewModel> reviewViewModels = reviewService
                .findAllReviewsByUser(principal.getUsername());

        return ResponseEntity
                .ok()
                .body(reviewViewModels);
    }
}
