package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.model.view.BookSummaryViewModel;
import bg.softuni.webbookstore.service.BookService;
import bg.softuni.webbookstore.service.WishlistService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final BookService bookService;

    public WishlistController(WishlistService wishlistService, BookService bookService) {
        this.wishlistService = wishlistService;
        this.bookService = bookService;
    }

    @GetMapping()
    public String showWishlist(Model model,
                               @AuthenticationPrincipal UserDetails principal) {

        getWishListBooksByCustomer(model, principal);

        return "wishlist";
    }

    @GetMapping("/add/{id}")
    public String addToWishlist(@PathVariable Long id,
                                Model model,
                                @AuthenticationPrincipal UserDetails principal) {

        wishlistService.addToWishlist(id, principal.getUsername());

        getWishListBooksByCustomer(model, principal);

        return "redirect:/wishlist";
    }

    @GetMapping("/remove/{id}")
    public String removeFromWishlist(@PathVariable Long id,
                                     Model model,
                                     @AuthenticationPrincipal UserDetails principal) {

        wishlistService.removeFromWishlist(id, principal.getUsername());

        getWishListBooksByCustomer(model, principal);

        return "redirect:/wishlist";
    }

    private void getWishListBooksByCustomer(Model model, @AuthenticationPrincipal UserDetails principal) {
        List<BookSummaryViewModel> wishlist = bookService
                .getWishListBooksByCustomer(principal.getUsername());

        model.addAttribute("booksCount", wishlist.size());
        model.addAttribute("books", wishlist);
    }
}
