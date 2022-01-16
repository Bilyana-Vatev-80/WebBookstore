package bg.softuni.webbookstore.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "cart_items")
public class CartItemEntity extends BaseEntity {

    @ManyToOne(optional = false)
    private BookEntity book;

    @ManyToOne(optional = false)
    private UserEntity customer;

    @Column(nullable = false)
    private Integer quantity;


    public CartItemEntity() {
    }

    public BookEntity getBook() {
        return book;
    }

    public CartItemEntity setBook(BookEntity book) {
        this.book = book;
        return this;
    }

    public UserEntity getCustomer() {
        return customer;
    }

    public CartItemEntity setCustomer(UserEntity customer) {
        this.customer = customer;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public CartItemEntity setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}
