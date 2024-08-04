package com.example.demo.DAO;

import com.example.demo.Entites.UserDetails;

import java.util.List;

public interface UserDetailsDAO {

    void save(UserDetails theuser);

    void updatefirstname(String firstname);

    void updatelastname(String lastname);

    void deleteById(int id);

    int deleteAll();

    List<UserDetails> findAll();
    
    UserDetails findById(int id);

    List<UserDetails> findByFirstName(String firstname);

    List<UserDetails> findByLastName(String lastname);

    UserDetails findByEmail(String email);
    

}
