package bg.softuni.webbookstore.model.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reviews")
public class ReviewEntity extends BaseEntity {

    @Column(nullable = false)
    private String nickname;

    private String title;

    @Lob
    private String textContent;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "added_on", nullable = false)
    private Instant addedOn;

    @Column(nullable = false)
    private Boolean approved;

    @ManyToOne
    private UserEntity author;

    @ManyToOne
    private BookEntity book;


    public ReviewEntity() {
    }

    public String getNickname() {
        return nickname;
    }

    public ReviewEntity setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ReviewEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getTextContent() {
        return textContent;
    }

    public ReviewEntity setTextContent(String textContent) {
        this.textContent = textContent;
        return this;
    }

    public Integer getRating() {
        return rating;
    }

    public ReviewEntity setRating(Integer rating) {
        this.rating = rating;
        return this;
    }

    public Instant getAddedOn() {
        return addedOn;
    }

    public ReviewEntity setAddedOn(Instant addedOn) {
        this.addedOn = addedOn;
        return this;
    }

    public Boolean getApproved() {
        return approved;
    }

    public ReviewEntity setApproved(Boolean approved) {
        this.approved = approved;
        return this;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public ReviewEntity setAuthor(UserEntity author) {
        this.author = author;
        return this;
    }

    public BookEntity getBook() {
        return book;
    }

    public ReviewEntity setBook(BookEntity book) {
        this.book = book;
        return this;
    }

    @PrePersist
    public void onCreate() {
        this.setAddedOn(Instant.now());
        this.setApproved(false);
    }
}

