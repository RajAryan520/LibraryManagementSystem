package com.example.demo.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http

                .authorizeHttpRequests(authorize -> authorize
                       // .requestMatchers("api/UserLogin/CreateNewUser").permitAll()
                        .requestMatchers( "/api/UserLogin/**","/api/UserLogin/CreateNewUser", "/api/UserLogin/AddUser", "/CreateUser", "/Login.html").permitAll()
                        .requestMatchers("/api/Books/BorrowBook","/api/Books/BooksBorrowed","/api/UserSection/UserAccount","/api/UserSection/UpdateInfo","/api/UserSection/UpdateAccountInfo","api/UserSection/DeleteAccount","api/UserSection/DeleteUserAccount").hasRole("USER")
                        .requestMatchers("/api/Books/AddBookAdmin","/api/Books/AddBook","api/Books/DeleteBook","/api/UserSection/**").hasRole("ADMIN")



                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                                .loginPage("/api/UserLogin/Login")
                                .loginProcessingUrl("/AuthenticateUser")
                                .defaultSuccessUrl("/api/UserLogin",true)
                                .permitAll()
                )
                        .exceptionHandling(exceptionHandling -> exceptionHandling
                                .accessDeniedHandler(accessDeniedHandler()));


                http.csrf(csrf -> csrf.disable());

                return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return ((request, response, accessDeniedException) -> {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            request.getRequestDispatcher("/api/UserLogin/error403").forward(request,response);
        });
    }
}
