package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.model.entity.BookEntity;
import bg.softuni.webbookstore.model.entity.UserEntity;
import bg.softuni.webbookstore.model.entity.WishlistItemEntity;
import bg.softuni.webbookstore.repository.BookRepository;
import bg.softuni.webbookstore.repository.UserRepository;
import bg.softuni.webbookstore.repository.WishlistRepository;
import bg.softuni.webbookstore.service.WishlistService;
import bg.softuni.webbookstore.web.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static bg.softuni.webbookstore.constant.GlobalConstants.*;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public WishlistServiceImpl(WishlistRepository wishlistRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addToWishlist(Long id, String username) {
        Optional<WishlistItemEntity> wishlistItem = wishlistRepository
                .findByBookIdAndCustomerUsername(id, username);

        if (wishlistItem.isEmpty()) {
            BookEntity book = bookRepository
                    .findByIdAndActiveTrue(id)
                    .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_BOOK));

            UserEntity customer = userRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_USER));

            WishlistItemEntity wishlistItemEntity = new WishlistItemEntity()
                    .setBook(book)
                    .setCustomer(customer);

            wishlistRepository.save(wishlistItemEntity);
        }
    }

    @Override
    public void removeFromWishlist(Long id, String username) {
        WishlistItemEntity wishlistItemEntity = wishlistRepository
                .findByBookIdAndCustomerUsername(id, username)
                .orElseThrow(() -> new ObjectNotFoundException(OBJECT_NAME_WISHLIST_ITEM));

        wishlistRepository
                .deleteById(wishlistItemEntity.getId());
    }

    @Override
    public void deleteBookFromAllWishlists(Long bookId) {
        wishlistRepository
                .deleteAllByBookId(bookId);
    }
}
