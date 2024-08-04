package com.example.demo.DAOImpl;

import com.example.demo.DAO.UserDetailsDAO;
import com.example.demo.Entites.UserDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserDetailsDAOImp implements UserDetailsDAO {


    // Field for entity Manager
    private EntityManager entityManager;

    // Constructor  injection

    public UserDetailsDAOImp(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(UserDetails theuser) {
        entityManager.persist(theuser);
    }

    @Override
    @Transactional
    public void updatefirstname(String firstname) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails user = findByEmail(username);
        user.setFirstname(firstname);
        entityManager.merge(user);
    }

    @Override
    @Transactional
    public void updatelastname(String lastname) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails user = findByEmail(username);
        user.setLastname(lastname);
        entityManager.merge(user);
    }


    @Override
    @Transactional
    public void deleteById(int id) {
        UserDetails theuser = entityManager.find(UserDetails.class,id);
        entityManager.remove(theuser);
    }

    @Override
    @Transactional
    public int deleteAll() {
        int rowDel = entityManager.createQuery("DELETE FROM UserDetails").executeUpdate();
        return rowDel;

    }

    @Override
    public List<UserDetails> findAll() {
        TypedQuery<UserDetails> theQuery = entityManager.createQuery(
                "SELECT u FROM UserDetails u ", UserDetails.class);

        try {
            return theQuery.getResultList();
        } catch (NoResultException e) {
            throw new NoResultException("No User found");
        }
    }

    @Override
    public UserDetails findById(int id) {
        UserDetails theuser =  entityManager.find(UserDetails.class,id);
        if (theuser == null) {
            throw new NoResultException("No user found with the given ID.");
        }
        return theuser;
    }

    @Override
    public List<UserDetails> findByFirstName(String firstname) {
        TypedQuery<UserDetails> theQuery = entityManager.createQuery(
                "SELECT u FROM UserDetails u WHERE u.firstname = :firstname", UserDetails.class);
        theQuery.setParameter("firstname",firstname);
        List<UserDetails> users = theQuery.getResultList();
        if (users.isEmpty()) {
            throw new NoResultException("No User found with the given FirstName.");
        }
        return users;
    }

    @Override
    public List<UserDetails> findByLastName(String lastname) {
        TypedQuery<UserDetails> theQuery = entityManager.createQuery(
                "SELECT u FROM UserDetails u WHERE u.lastname = :lastname", UserDetails.class);
        theQuery.setParameter("lastname",lastname);
        List<UserDetails> users = theQuery.getResultList();
        if (users.isEmpty()) {
            throw new NoResultException("No User found with the given LastName.");
        }
        return users;
    }

    @Override
    public UserDetails findByEmail(String email) {
        TypedQuery<UserDetails> theQuery = entityManager.createQuery(
                "SELECT u FROM UserDetails u WHERE u.email = :email", UserDetails.class);
        theQuery.setParameter("email",email);
        try {
            return theQuery.getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException("No User found with the given Email.");
        }
    }
}
