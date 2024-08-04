package com.example.demo.Controller;

import com.example.demo.DAO.BookDetailsDAO;
import com.example.demo.DAO.UserDetailsDAO;
import com.example.demo.Entites.BookDetails;
import com.example.demo.Entites.UserDetails;
import com.example.demo.Security.UserService;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/UserSection")
public class UsersSectionController {

    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    @Autowired
    private UserDetailsDAO userDetailsDAO;

    @Autowired
    private BookDetailsDAO bookDetailsDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/AllUser")
    public String AllUser(Model model){
        String alertMessage = null;
        List<UserDetails> users = new ArrayList<>();
        try {
            users = userDetailsDAO.findAll();
            if (users.isEmpty()) {
                alertMessage = "No users found.";
            }
        } catch (Exception e) {
            alertMessage = "An error occurred: " + e.getMessage();
        }
        model.addAttribute("users", users);
        model.addAttribute("alertMessage", alertMessage);
        return "SearchUserResult";
    }


    @GetMapping("/DeleteUser")
    public String deleteuser(){
        return "DeleteUser";
    }

    @PostMapping("/DeleteUserAdmin")
    public String DeleteUser(@RequestParam("UserID") String UserId){
        int id = Integer.parseInt(UserId);
        try{
            List<BookDetails> books = bookDetailsDAO.booksBorrowed();
            for (BookDetails book :books) {
                bookDetailsDAO.returnBook(book.getId());
            }
        }
        catch (EmptyResultDataAccessException | NoResultException e){
            System.out.println(e.getMessage());
        }
        UserDetails theuser = userDetailsDAO.findById(id);
        userDetailsDAO.deleteById(id);

        jdbcTemplate.update("DELETE FROM authorities WHERE username = ?",theuser.getEmail());
        jdbcTemplate.update("DELETE FROM users WHERE username = ?",theuser.getEmail());
        return "redirect:/api/UserSection/AllUser";
    }

    @GetMapping("/DeleteAll")
    public String deleteall(){
        return "DeleteAllUser";
    }

    @GetMapping("/DeleteAllUser")
    public String DeleteAllUser(){
        jdbcTemplate.update("UPDATE bookdetails SET user_id = NULL");
        jdbcTemplate.update("UPDATE bookdetails SET borrowed = 0");
        userDetailsDAO.deleteAll();
        jdbcTemplate.update("DELETE FROM authorities WHERE authority = 'ROLE_USER'");
        jdbcTemplate.update("DELETE FROM users WHERE username NOT IN (SELECT username FROM authorities WHERE authority = 'ROLE_ADMIN')");
        return "redirect:/api/UserSection/AllUser";
    }

    @GetMapping("/SearchUser")
    public String searchuser(){
        return "SearchUser";
    }

    @GetMapping("/Search")
    public String search(@RequestParam String SearchType, @RequestParam String SearchValue,Model model) {
        List<UserDetails> users = new ArrayList<>();
        UserDetails theuser;
        String alertMessage = null;
        try {
            switch (SearchType) {
                case "id":
                    theuser = userDetailsDAO.findById(Integer.parseInt(SearchValue));
                    if (theuser != null) users.add(theuser);
                    break;

                case "firstname":
                    users = userDetailsDAO.findByFirstName(SearchValue);
                    break;

                case "lastname":
                    users = userDetailsDAO.findByLastName(SearchValue);
                    break;

                case "email":
                    theuser = userDetailsDAO.findByEmail(SearchValue);
                    if (theuser != null) users.add(theuser);
                    break;
            }
        } catch (NoResultException e) {
            alertMessage = e.getMessage();
        } catch (Exception e) {
            alertMessage = "An error occurred: " + e.getMessage();
        }

        model.addAttribute("users", users);
        model.addAttribute("alertMessage", alertMessage);
        return "SearchUserResult";
    }

    @GetMapping("/UpdateInfo")
    public String updateinfo(){
        return "UpdateUserInfo";
    }

    @GetMapping("/UpdateAccountInfo")
    public String UpdateUser(@RequestParam String SearchType, @RequestParam String SearchValue){

            switch (SearchType){
                case "firstname":
                    userDetailsDAO.updatefirstname(SearchValue);
                    break;
                case "lastname":
                    userDetailsDAO.updatelastname(SearchValue);
                    break;
            }

        return "redirect:/api/UserSection/UserAccount";
    }

    @GetMapping("/UserAccount")
    public String account(Model model){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails theuser = userDetailsDAO.findByEmail(username);
        model.addAttribute("user",theuser);
        return "AccountInfo";
    }

    @GetMapping("/DeleteAccount")
    public String deleteAccount(){
        return "DeleteAccount";
    }

    @GetMapping("/DeleteUserAccount")
    public String deleteuseraccount(HttpServletRequest request, HttpServletResponse response){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails theuser = userDetailsDAO.findByEmail(username);
        try{
            List<BookDetails> books = bookDetailsDAO.booksBorrowed();
                for (BookDetails book :books) {
                    bookDetailsDAO.returnBook(book.getId());
                }
        }
        catch (EmptyResultDataAccessException | NoResultException e){
            System.out.println(e.getMessage());
        }

        userDetailsDAO.deleteById(theuser.getId());
        this.logoutHandler.logout(request,response,SecurityContextHolder.getContext().getAuthentication());
        jdbcTemplate.update("DELETE FROM authorities WHERE username = ?",username);
        jdbcTemplate.update("DELETE FROM users WHERE username = ?",username);

        return "redirect:/api/UserLogin";
    }

    @GetMapping("/AllAdmin")
    public String alladmin(Model model){
        String sql = "SELECT u.username FROM users u INNER JOIN authorities a ON u.username = a.username WHERE a.authority = 'ROLE_ADMIN'";
        List<String> adminUsernames = jdbcTemplate.queryForList(sql, String.class);

        model.addAttribute("adminUsernames", adminUsernames);
        return "DisplayAdmin";
    }

    @GetMapping("/CreateAdmin")
    public String Createadmin(){
        return "CreateAdmin";
    }

    @PostMapping("/AddAdmin")
    public String AddAdmin( @RequestParam("Email") String email, @RequestParam("Password") String password){
        userService.createUser(email,password,"ADMIN");
        return "redirect:/api/UserSection/AllAdmin";
    }
}
