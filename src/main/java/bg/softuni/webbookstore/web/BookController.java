package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.model.binding.BookAddBindingModel;
import bg.softuni.webbookstore.model.binding.BookUpdateBindingModel;
import bg.softuni.webbookstore.model.entity.enums.CategoryEnum;
import bg.softuni.webbookstore.model.entity.enums.LanguageEnum;
import bg.softuni.webbookstore.model.service.BookAddServiceModel;
import bg.softuni.webbookstore.model.service.BookUpdateServiceModel;
import bg.softuni.webbookstore.model.view.BookDetailViewModel;
import bg.softuni.webbookstore.service.*;
import bg.softuni.webbookstore.web.exception.ObjectNotFoundException;
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
import java.io.IOException;

import static bg.softuni.webbookstore.constant.GlobalConstants.*;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final PublishingHouseService publishingHouseService;
    private final ShoppingCartService shoppingCartService;
    private final WishlistService wishlistService;
    private final ReviewService reviewService;
    private final PagesViewCountService pagesViewCountService;
    private final ModelMapper modelMapper;

    public BookController(BookService bookService, PublishingHouseService publishingHouseService, ShoppingCartService shoppingCartService, WishlistService wishlistService, ReviewService reviewService, PagesViewCountService pagesViewCountService, ModelMapper modelMapper) {
        this.bookService = bookService;
        this.publishingHouseService = publishingHouseService;
        this.shoppingCartService = shoppingCartService;
        this.wishlistService = wishlistService;
        this.reviewService = reviewService;
        this.pagesViewCountService = pagesViewCountService;
        this.modelMapper = modelMapper;
    }

    @ModelAttribute("bookAddBindingModel")
    public BookAddBindingModel bookAddBindingModel() {
        return new BookAddBindingModel();
    }

    @ModelAttribute("bookUpdateBindingModel")
    public BookUpdateBindingModel bookUpdateBindingModel() {
        return new BookUpdateBindingModel();
    }

    //GET
    @GetMapping("/all")
    public String allBooks(Model model) {
        model.addAttribute("books", bookService.findAllBooks());
        return "all-books";
    }

    @GetMapping("/order-by-title")
    public String allBooksByTitle(Model model) {
        model.addAttribute("books", bookService.findAllBooksOrderByTitle());
        return "all-books";
    }

    @GetMapping("/order-by-author")
    public String allBooksByAuthor(Model model) {
        model.addAttribute("books", bookService.findAllBooksOrderByAuthor());
        return "all-books";
    }

    @GetMapping("/best-selling")
    public String allBooksByBestSelling(Model model) {
        model.addAttribute("books", bookService.findAllBooksOrderByBestSelling());
        return "all-books";
    }

    //CREATE
    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("languages", LanguageEnum.values());
        model.addAttribute("categories", CategoryEnum.values());
        model.addAttribute("publishingHouses", publishingHouseService.findAllPublishingHouseNames());
        return "add-book";
    }

    @PostMapping("/add")
    public String addConfirm(@Valid BookAddBindingModel bookAddBindingModel,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal UserDetails principal) throws IOException {

        if (bindingResult.hasErrors()) {
            redirectAttributes
                    .addFlashAttribute("bookAddBindingModel", bookAddBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.bookAddBindingModel", bindingResult);

            return "redirect:/books/add";
        }

        BookAddServiceModel bookAddServiceModel = modelMapper
                .map(bookAddBindingModel, BookAddServiceModel.class);

        bookAddServiceModel.setCreator(principal.getUsername());

        Long bookId = bookService.add(bookAddServiceModel);

        redirectAttributes.addFlashAttribute("addedSuccessfully", true);
        return "redirect:/books/" + bookId;
    }

    //DETAILS
    @GetMapping("/{id}")
    public String details(@PathVariable Long id,
                          Model model) {

        BookDetailViewModel detailViewModel = bookService
                .findBookDetails(id)
                .orElseThrow(() ->
                        new ObjectNotFoundException(OBJECT_NAME_BOOK));

        model.addAttribute("book", detailViewModel);
        model.addAttribute("viewsCount", pagesViewCountService.getPageViewsCount(String.format(VIEWS_COUNT_URI, id)));

        return "book-details";
    }

    //UPDATE
    @PreAuthorize("isAdmin()")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       Model model) {

        BookUpdateBindingModel bookUpdateBindingModel = bookService
                .findBookToEdit(id)
                .orElseThrow(() ->
                        new ObjectNotFoundException(OBJECT_NAME_BOOK));

        model.addAttribute("bookUpdateBindingModel", bookUpdateBindingModel);
        model.addAttribute("languages", LanguageEnum.values());
        model.addAttribute("categories", CategoryEnum.values());
        model.addAttribute("publishingHouses", publishingHouseService.findAllPublishingHouseNames());

        return "edit-book";
    }

    @PreAuthorize("isAdmin()")
    @GetMapping("/edit/{id}/errors")
    public String editConfirmErrors(@PathVariable Long id, Model model) {

        model.addAttribute("languages", LanguageEnum.values());
        model.addAttribute("categories", CategoryEnum.values());
        model.addAttribute("publishingHouses", publishingHouseService.findAllPublishingHouseNames());

        return "edit-book";
    }

    @PreAuthorize("isAdmin()")
    @PatchMapping("/edit/{id}")
    public String editConfirm(@PathVariable Long id,
                              @Valid BookUpdateBindingModel bookUpdateBindingModel,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            redirectAttributes
                    .addFlashAttribute("bookUpdateBindingModel", bookUpdateBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.bookUpdateBindingModel", bindingResult);

            return "redirect:/books/edit/" + id + "/errors";
        }

        BookUpdateServiceModel updateServiceModel = modelMapper
                .map(bookUpdateBindingModel, BookUpdateServiceModel.class);
        updateServiceModel.setId(id);

        bookService.update(updateServiceModel);

        redirectAttributes.addFlashAttribute("updatedSuccessfully", true);
        return "redirect:/books/" + id;
    }

    //DELETE
    @PreAuthorize("isAdmin()")
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        wishlistService.deleteBookFromAllWishlists(id);
        shoppingCartService.deleteBookFromAllShoppingCarts(id);
        reviewService.deleteAllReviewsForBook(id);
        bookService.delete(id);
        return "redirect:/books/all";
    }
}
