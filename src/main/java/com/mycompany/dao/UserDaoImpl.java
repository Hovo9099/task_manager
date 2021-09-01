package com.mycompany.dao;

import com.mycompany.entity.User;
import com.mycompany.models.UserModel;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao{

    @Autowired
    private SessionFactory sessionFactory;


    @Override
    public void persist(User entity) {
        sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(User entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public User findById(Integer id) {
        User user = (User) sessionFactory.getCurrentSession().createQuery("from User where id = :id").setParameter("id", id).getSingleResult();
        return user;
    }

    @Override
    public void delete(User entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public List<User> findAll() {
       List<User> user = (List<User>) sessionFactory.getCurrentSession().createQuery("from User").list();
       return user;
    }

    @Override
    public void deleteAll() {
        List<User> listUser = findAll();
        for (User entity : listUser) {
            delete(entity);
        }
    }

}
