package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.model.entity.BookEntity;
import bg.softuni.webbookstore.model.entity.CartItemEntity;
import bg.softuni.webbookstore.model.entity.UserEntity;
import bg.softuni.webbookstore.model.view.CartItemViewModel;
import bg.softuni.webbookstore.repository.BookRepository;
import bg.softuni.webbookstore.repository.CartItemRepository;
import bg.softuni.webbookstore.repository.UserRepository;
import bg.softuni.webbookstore.service.BookService;
import bg.softuni.webbookstore.service.ShoppingCartService;
import bg.softuni.webbookstore.web.exception.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static bg.softuni.webbookstore.constant.GlobalConstants.*;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    public ShoppingCartServiceImpl(CartItemRepository cartItemRepository, UserRepository userRepository, BookRepository bookRepository, BookService bookService, ModelMapper modelMapper) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<CartItemViewModel> getCartItemsByCustomer(String username) {
        return cartItemRepository
                .findAllByCustomerUsername(username)
                .stream()
                .map(cartItemEntity -> modelMapper
                        .map(cartItemEntity, CartItemViewModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public void addToCart(Long id, String username) {
        Optional<CartItemEntity> cartItemEntity = cartItemRepository
                .findByBookIdAndCustomerUsername(id, username);

        if (cartItemEntity.isPresent()) {
            BookEntity book = cartItemEntity.get().getBook();

            if (book.getCopies() > 0) {
                bookService.decreaseWithOneCopy(book.getId());

                Integer newQuantity = cartItemEntity.get().getQuantity() + 1;
                cartItemEntity.get().setQuantity(newQuantity);
                cartItemRepository.save(cartItemEntity.get());
            }
        } else {
            BookEntity book = bookRepository
                    .findByIdAndActiveTrue(id)
                    .orElseThrow(() ->
                            new ObjectNotFoundException(OBJECT_NAME_BOOK));
            bookService.decreaseWithOneCopy(id);

            UserEntity customer = userRepository.findByUsername(username)
                    .orElseThrow(() ->
                            new ObjectNotFoundException(OBJECT_NAME_USER));

            CartItemEntity newCartItemEntity = new CartItemEntity()
                    .setQuantity(1)
                    .setBook(book)
                    .setCustomer(customer);

            cartItemRepository.save(newCartItemEntity);
        }
    }

    @Override
    public void removeFromCart(Long id, String username) {
        CartItemEntity cartItemEntity = cartItemRepository
                .findByBookIdAndCustomerUsername(id, username)
                .orElseThrow(() ->
                        new ObjectNotFoundException(OBJECT_NAME_CART_ITEM));

        BookEntity book = cartItemEntity.getBook();
        book.setCopies(
                book.getCopies() + cartItemEntity.getQuantity()
        );
        bookRepository.save(book);

        cartItemRepository
                .deleteById(cartItemEntity.getId());
    }

    @Override
    public void decreaseQuantity(Long id, String username) {
        CartItemEntity cartItemEntity = cartItemRepository
                .findByBookIdAndCustomerUsername(id, username)
                .orElseThrow(() ->
                        new ObjectNotFoundException(OBJECT_NAME_CART_ITEM));

        if (cartItemEntity.getQuantity() > 1) {
            BookEntity book = cartItemEntity.getBook();
            bookService.increaseWithOneCopy(book.getId());

            Integer newQuantity = cartItemEntity.getQuantity() - 1;
            cartItemEntity.setQuantity(newQuantity);

            cartItemRepository.save(cartItemEntity);
        }
    }

    @Override
    public void deleteOrderedCardItems(String username) {
        cartItemRepository
                .deleteAllByCustomerUsername(username);
    }

    @Override
    public void deleteBookFromAllShoppingCarts(Long bookId) {
        cartItemRepository
                .deleteAllByBookId(bookId);
    }
}
