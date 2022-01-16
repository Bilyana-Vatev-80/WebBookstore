package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.model.binding.AuthorAddBindingModel;
import bg.softuni.webbookstore.model.service.AuthorAddServiceModel;
import bg.softuni.webbookstore.model.view.AuthorViewModel;
import bg.softuni.webbookstore.service.AuthorService;
import bg.softuni.webbookstore.service.BookService;
import bg.softuni.webbookstore.web.exception.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;

import static bg.softuni.webbookstore.constant.GlobalConstants.*;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    public AuthorController(AuthorService authorService, BookService bookService, ModelMapper modelMapper) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @ModelAttribute("authorAddBindingModel")
    public AuthorAddBindingModel authorAddBindingModel() {
        return new AuthorAddBindingModel();
    }

    @GetMapping("/add")
    public String add() {
        return "add-author";
    }

    @PostMapping("/add")
    public String addConfirm(@Valid AuthorAddBindingModel authorAddBindingModel,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            redirectAttributes
                    .addFlashAttribute("authorAddBindingModel", authorAddBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.authorAddBindingModel", bindingResult);

            return "redirect:/authors/add";
        }

        AuthorAddServiceModel authorAddServiceModel = modelMapper
                .map(authorAddBindingModel, AuthorAddServiceModel.class);

        Long authorId = authorService.add(authorAddServiceModel);

        redirectAttributes.addFlashAttribute("addedSuccessfully", true);
        return "redirect:/authors/" + authorId;
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id,
                          Model model) {

        AuthorViewModel viewModel = authorService
                .findById(id)
                .orElseThrow(() ->
                        new ObjectNotFoundException(OBJECT_NAME_AUTHOR));

        model.addAttribute("author", viewModel);
        model.addAttribute("books", bookService.findBooksByAuthor(id));

        return "author-details";
    }
}
