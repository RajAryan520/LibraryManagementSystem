package com.example.demo.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(String username, String password,String role){
        String encodedPassword = passwordEncoder.encode(password);

        User.UserBuilder userBuilder = User.withUsername(username).password(encodedPassword).roles(role);
        userDetailsManager.createUser(userBuilder.build());

//        UserDetails userDetails = User.withUsername(username).password(encodedPassword).roles(role).build();
//        ((JdbcUserDetailsManager) userDetailsService).createUser(userDetails);
    }
}
