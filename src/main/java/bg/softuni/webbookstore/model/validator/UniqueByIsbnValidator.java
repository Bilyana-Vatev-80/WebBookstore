package bg.softuni.webbookstore.model.validator;

import bg.softuni.webbookstore.service.BookService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueByIsbnValidator implements ConstraintValidator<UniqueByIsbn, String> {

    private final BookService bookService;

    public UniqueByIsbnValidator(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null) {
            return true;
        }

        return !bookService.existsByIsbn(isbn);
    }
}
