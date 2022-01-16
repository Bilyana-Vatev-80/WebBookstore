package bg.softuni.webbookstore.service;

import bg.softuni.webbookstore.model.view.CartItemViewModel;

import java.util.List;

public interface ShoppingCartService {

    List<CartItemViewModel> getCartItemsByCustomer(String username);

    void addToCart(Long bookId, String username);

    void removeFromCart(Long id, String username);

    void decreaseQuantity(Long id, String username);

    void deleteOrderedCardItems(String username);

    void deleteBookFromAllShoppingCarts(Long bookId);
}
