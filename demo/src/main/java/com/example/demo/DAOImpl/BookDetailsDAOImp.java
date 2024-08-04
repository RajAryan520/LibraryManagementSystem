package com.example.demo.DAOImpl;

import com.example.demo.DAO.BookDetailsDAO;
import com.example.demo.DAO.UserDetailsDAO;
import com.example.demo.Entites.BookDetails;
import com.example.demo.Entites.UserDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Repository
public class BookDetailsDAOImp implements BookDetailsDAO {

    private EntityManager entityManager;

    public BookDetailsDAOImp(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Autowired
    private UserDetailsDAO userDetailsDAO;

    @Autowired
    private MessageSource messageSource;

    @Override
    @Transactional
    public void save(BookDetails thebook) {
        entityManager.persist(thebook);
    }

    @Override
    public List<BookDetails> findAll() {
        TypedQuery<BookDetails> theQuery = entityManager.createQuery(
                "SELECT b FROM BookDetails b ", BookDetails.class);
        try {
            return theQuery.getResultList();
        } catch (NoResultException e) {
            throw new NoResultException("No book found");
        }
    }

    @Override
    public BookDetails findById(int Id) {
        BookDetails thebook = entityManager.find(BookDetails.class,Id);
        if (thebook == null) {
            throw new NoResultException("No book found with the given ID.");
        }
        return thebook;
    }

    @Override
    @Transactional
    public void deleteById(int Id) {
        BookDetails thebook = entityManager.find(BookDetails.class,Id);
        entityManager.remove(thebook);
    }

    @Override
    public BookDetails findByISBN(Long ISBN) {
        TypedQuery<BookDetails> theQuery = entityManager.createQuery(
                "SELECT b FROM BookDetails b WHERE b.isbn = :isbn", BookDetails.class);
        theQuery.setParameter("isbn",ISBN);
        try {
            return theQuery.getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException("No book found with the given ISBN.");
        }
    }

    @Override
    public List<BookDetails> findByAuthor(String Author) {
        TypedQuery<BookDetails> theQuery = entityManager.createQuery(
                "SELECT b FROM BookDetails b WHERE b.author = :author", BookDetails.class);
        theQuery.setParameter("author",Author);
        List<BookDetails> books = theQuery.getResultList();
        if (books.isEmpty()) {
            throw new NoResultException("No Book found by given Author");
        }
        return books;
    }

    @Override
    public List<BookDetails> findByTitle(String Title) {
        TypedQuery<BookDetails> theQuery = entityManager.createQuery(
                "SELECT b FROM BookDetails b WHERE b.title = :title", BookDetails.class);
        theQuery.setParameter("title",Title);
        List<BookDetails> books = theQuery.getResultList();
        if (books.isEmpty()) {
            throw new NoResultException("No Book found by given Title");
        }
        return books;
    }

    @Override
    @Transactional
    public void borrowBook(int Id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails theuser = userDetailsDAO.findByEmail(username);

        BookDetails thebook = findById(Id);
        if (thebook.isBorrowed() == false){
            thebook.setBorrowed(true);
            thebook.setBorrowedBy(theuser);
        }
        else {
            throw new RuntimeException("Book is already Borrowed ");
        }
        save(thebook);

    }

    @Override
    @Transactional
    public void returnBook(int Id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails theuser = userDetailsDAO.findByEmail(username);
        BookDetails thebook = findById(Id);
        if (thebook.getBorrowedBy() == theuser){
            thebook.setBorrowed(false);
            thebook.setBorrowedBy(null);
        } else if (thebook.getBorrowedBy() != theuser) {
            throw new RuntimeException("Book is Not Borrowed By the User");
        }
        save(thebook);
    }

    @Override
    public List<BookDetails> booksBorrowed() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails theuser = userDetailsDAO.findByEmail(username);
        TypedQuery<BookDetails> theQuery = entityManager.createQuery(
                "Select b FROM BookDetails b WHERE b.borrowedBy = :borrowedBy",BookDetails.class);
        theQuery.setParameter("borrowedBy",theuser);
        List<BookDetails> books = theQuery.getResultList();
        if(books.isEmpty()){
            throw new NoResultException("No Book Borrowed");
        }
        return books;
    }

    @Override
    public List<BookDetails> booksBorrowedById(int id) {
        UserDetails theuser = userDetailsDAO.findById(id);
        TypedQuery<BookDetails> theQuery = entityManager.createQuery(
                "Select b FROM BookDetails b WHERE b.borrowedBy = :borrowedBy",BookDetails.class);
        theQuery.setParameter("borrowedBy",theuser);
        List<BookDetails> books = theQuery.getResultList();
        if(books.isEmpty()){
            throw new NoResultException("No Book Borrowed");
        }
        return books;
    }
}
