package com.example.demo.DAO;


import com.example.demo.Entites.BookDetails;
import java.util.List;

public interface BookDetailsDAO {

    void save(BookDetails thebook);

    List<BookDetails> findAll();

    BookDetails findById(int Id);

    void deleteById(int Id);

    BookDetails findByISBN(Long ISBN);

    List<BookDetails> findByAuthor(String Author);

    List<BookDetails> findByTitle(String Title);

    void borrowBook(int Id);

    void returnBook(int Id);

    List<BookDetails> booksBorrowed();

    List<BookDetails> booksBorrowedById(int id);


}
