package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.service.BookService;
import bg.softuni.webbookstore.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final OrderService orderService;
    private final BookService bookService;

    public DashboardController(OrderService orderService, BookService bookService) {
        this.orderService = orderService;
        this.bookService = bookService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("soldBooksPerMonth", orderService.getOrderedBooksPerMonthMap());
        model.addAttribute("incomePerMonth", orderService.getIncomePerMonth());
        model.addAttribute("categoriesMap", bookService.getBookCategoriesMap());
        model.addAttribute("bookTitles", bookService.findAllBookTitlesWithTwoOrLessCopies());
        return "dashboard";
    }
}
