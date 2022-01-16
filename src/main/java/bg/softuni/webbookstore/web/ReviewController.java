package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.model.binding.ReviewAddBindingModel;
import bg.softuni.webbookstore.model.service.ReviewAddServiceModel;
import bg.softuni.webbookstore.model.view.ReviewViewModel;
import bg.softuni.webbookstore.service.BookService;
import bg.softuni.webbookstore.service.ReviewService;
import bg.softuni.webbookstore.web.exception.InvalidOrderException;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

import static bg.softuni.webbookstore.constant.GlobalConstants.CANNOT_UPDATE_ORDER_STATUS_ERROR_MESSAGE;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    public ReviewController(ReviewService reviewService, BookService bookService, ModelMapper modelMapper) {
        this.reviewService = reviewService;
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @ModelAttribute("reviewAddBindingModel")
    public ReviewAddBindingModel reviewAddBindingModel() {
        return new ReviewAddBindingModel();
    }

    @GetMapping("/not-approved")
    public String getNotApprovedReviews(Model model) {
        List<ReviewViewModel> reviews = reviewService.findAllNotApprovedReviews();
        model.addAttribute("reviews", reviews);

        return "not-approved-reviews";
    }

    @GetMapping("/add/{bookId}")
    public String add(@PathVariable Long bookId,
                      Model model) {

        model.addAttribute("bookTitle", bookService.findBookTitleById(bookId));
        model.addAttribute("bookId", bookId);
        return "add-review";
    }

    @PostMapping("/add")
    public String addConfirm(@Valid ReviewAddBindingModel reviewAddBindingModel,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal UserDetails principal) {

        if (bindingResult.hasErrors()) {
            redirectAttributes
                    .addFlashAttribute("reviewAddBindingModel", reviewAddBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.reviewAddBindingModel", bindingResult);

            return "redirect:/reviews/add/" + reviewAddBindingModel.getBookId();
        }

        ReviewAddServiceModel serviceModel = modelMapper
                .map(reviewAddBindingModel, ReviewAddServiceModel.class)
                .setBookNum(reviewAddBindingModel.getBookId())
                .setAuthor(principal.getUsername());

        reviewService.add(serviceModel);

        return "redirect:/books/" + reviewAddBindingModel.getBookId();
    }

    @PreAuthorize("isAdmin()")
    @GetMapping("/approve/{id}")
    public String approve(@PathVariable Long id) {
        reviewService.approve(id);
        return "redirect:/reviews/not-approved";
    }

    // TODO - implement delete functionality
//    @PreAuthorize("isAdmin()")
//    @DeleteMapping("/{bookId}/{reviewId}")
//    public String delete(@PathVariable Long bookId, @PathVariable Long reviewId) {
//        reviewService.delete(reviewId);
//        return "redirect:/books/" + bookId;
//    }
}
