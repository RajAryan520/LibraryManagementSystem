package com.example.demo.Entites;

import com.example.demo.Entites.UserDetails;
import jakarta.persistence.*;

@Entity
@Table(name = "bookdetails")

public class BookDetails {

    // Define Field

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name="id")
    private int id;

    @Column(name="title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "isbn")
    private Long isbn;

    @Column(name = "borrowed")
    private boolean borrowed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDetails borrowedBy;


    // Empty Constructor
    public BookDetails(){

    }



    // Argument Constructor

    public BookDetails(String title, String author, Long isbn, boolean borrowed) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.borrowed = borrowed;
    }

    // Getter and Setter

    public int getId() {
        return id;
    }

    public UserDetails getBorrowedBy() {
        return borrowedBy;
    }

    public void setBorrowedBy(UserDetails borrowedBy) {
        this.borrowedBy = borrowedBy;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getIsbn() {
        return isbn;
    }

    public void setIsbn(Long isbn) {
        this.isbn = isbn;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    @Override
    public String toString() {
        return "BookDetails{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn=" + isbn +
                ", borrowed=" + borrowed +
                '}';
    }
}

