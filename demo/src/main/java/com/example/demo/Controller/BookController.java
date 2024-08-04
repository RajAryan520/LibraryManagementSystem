package com.example.demo.Controller;

import com.example.demo.DAO.BookDetailsDAO;
import com.example.demo.Entites.BookDetails;
import com.example.demo.Entites.UserDetails;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/api/Books")
public class BookController {

    @Autowired
    private BookDetailsDAO bookDetailsDAO;

    @GetMapping("/SearchBooks")
    public String searchbooks(){
        return "SearchBooks";
    }

    @GetMapping("/search")
    public String findById(@RequestParam String SearchType,@RequestParam String SearchValue, Model model){
        List<BookDetails> books = new ArrayList<>();
        BookDetails thebook;
        String alertMessage = null;

        try {

            switch (SearchType) {
                case "id":
                        thebook = bookDetailsDAO.findById(Integer.parseInt(SearchValue));
                        if (thebook != null) books.add(thebook);
                    break;
                case "isbn":
                        thebook = bookDetailsDAO.findByISBN(Long.parseLong(SearchValue));
                        if (thebook != null) books.add(thebook);
                    break;
                case "author":
                        books = bookDetailsDAO.findByAuthor(SearchValue);
                    break;
                case "title":
                        books = bookDetailsDAO.findByTitle(SearchValue);
                    break;
            }
        }
        catch (NoResultException e) {
            alertMessage = e.getMessage();
        } catch (Exception e) {
            alertMessage = "An error occurred: " + e.getMessage();
        }

        model.addAttribute("books",books);
        model.addAttribute("alertMessage",alertMessage);
        return "SearchResult";
    }

    @GetMapping("/AllBooks")
    public String allBooks(Model model) {
        String alertMessage = null;
        List<BookDetails> books = new ArrayList<>();
        try {
            books = bookDetailsDAO.findAll();
            if (books.isEmpty()) {
                alertMessage = "No books found.";
            }
        } catch (Exception e) {
            alertMessage = "An error occurred: " + e.getMessage();
        }
        model.addAttribute("books", books);
        model.addAttribute("alertMessage", alertMessage);
        return "SearchResult";
    }
//
//    @GetMapping("/AllBookDetails")
//    public String allBooksdetails(String alertMessage,Model model) {
//        List<BookDetails> books = new ArrayList<>();
//        try {
//            books = bookDetailsDAO.findAll();
//            if (books.isEmpty()) {
//                alertMessage = "No book in the Library!!";
//            }
//        } catch (Exception e) {
//            alertMessage = "An error occurred: " + e.getMessage();
//        }
//        model.addAttribute("books", books);
//        model.addAttribute("alertMessage", alertMessage);
//        return "SearchResult";
//    }

    @GetMapping("/BorrowBook")
    public String borrowbook(){
        return "BorrowBook";
    }

    @GetMapping("/BorrowBookById")
    public String BorrowBook(@RequestParam("BookID") String BookId,Model model)
    {
        int id = Integer.parseInt(BookId);
        String alertMessage = null;
        try{
            bookDetailsDAO.borrowBook(id);
        }
        catch (RuntimeException e){
            alertMessage = e.getMessage();
        }
        List<BookDetails> books = bookDetailsDAO.booksBorrowed();
        model.addAttribute("books",books);
        model.addAttribute("alertMessage",alertMessage);
        return "BooksBorrowed";

    }

    @GetMapping("/AddBook")
    public String addbook(){
        return "AddBook";
    }


    @PostMapping("/AddBookAdmin")
    public String AddBook(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("isbn") Long isbn,Model model)
    {
        BookDetails thebook = new BookDetails(title,author,isbn,false);
        bookDetailsDAO.save(thebook);
        List<BookDetails> books = bookDetailsDAO.findAll();
        model.addAttribute("books",books);
        model.addAttribute("alertMessage",null);
        return "redirect:/api/Books/AllBooks";
    }

    @GetMapping("/DeleteBook")
    public String deletebook(){
        return "DeleteBook";
    }

    @PostMapping("/DeleteBookAdmin")
    public String DeleteBook(@RequestParam("BookID") String BookId,Model model)
    {
        int id = Integer.parseInt(BookId);
        String alertMessage = null;
        try{
            bookDetailsDAO.deleteById(id);
        }
        catch (RuntimeException e){
            alertMessage = e.getMessage();
        }
        return "redirect:/api/Books/AllBooks";
    }


    @GetMapping("/BooksBorrowed")
    public String bookBorrowed(Model model){
        String alertMessage = null;
        List<BookDetails> books = new ArrayList<>();
        try {
            books = bookDetailsDAO.booksBorrowed();
        }
        catch (NoResultException e) {
        alertMessage = e.getMessage();
        }
        catch (Exception e) {
        alertMessage = "An error occurred: " + e.getMessage();
        }
        model.addAttribute("alertMessage",alertMessage);
        model.addAttribute("books",books);
        return "SearchResult";
    }

    @GetMapping("/ReturnBook")
    public String returnbook(){
        return "ReturnBook";
    }

    @GetMapping("/ReturnBookUser")
    public String ReturnBookUser(@RequestParam String BookID,Model model){
        String alertMessage = null;
        try{
            bookDetailsDAO.returnBook(Integer.parseInt(BookID));
        }
        catch (RuntimeException e){
            alertMessage = e.getMessage();
        }
        catch (Exception e) {
            alertMessage = "An error occurred: " + e.getMessage();
        }
        return "redirect:/api/Books/BooksBorrowed";

    }
}
