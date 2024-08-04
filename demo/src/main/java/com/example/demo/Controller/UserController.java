package com.example.demo.Controller;

import com.example.demo.DAO.UserDetailsDAO;
import com.example.demo.Entites.UserDetails;
import com.example.demo.Security.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("api/UserLogin")
public class UserController {

    @Autowired
    private UserDetailsDAO userdao;

    @Autowired
    private UserService userService;

    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();

    @GetMapping
    public String Home(){
        return "Home.html";
    }

    @GetMapping("/error403")
    public String Error403(){
        return "error403";
    }

    @GetMapping("/CreateNewUser")
    public String NewUser(){
        return "CreateUser.html";
    }

    @GetMapping("/Login")
    public String LoginForm(){
        return "Login.html";
    }

    @GetMapping("/Logout")
    public String Logout(HttpServletResponse response, HttpServletRequest request){
        this.logoutHandler.logout(request,response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/api/UserLogin";
    }

    // CreateUser Form Details

    @PostMapping("/AddUser")
    public String AddUser(
            @RequestParam("First_name") String firstname,
            @RequestParam("Last_name") String lastname,
            @RequestParam("Email") String email,
            @RequestParam("Password") String password)
    {
        UserDetails newuser = new UserDetails(firstname,lastname,email);
        userdao.save(newuser);

        //Create User with Role : "USER"
        userService.createUser(email,password,"USER");

        return "Login.html";
    }

}
