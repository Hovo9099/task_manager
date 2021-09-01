package com.mycompany.dao;

import com.mycompany.entity.Task;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TaskDaoImpl implements TaskDao {

    @Autowired
    private SessionFactory sessionFactory;


    @Override
    public void persist(Task entity) {
        sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(Task entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Transactional
    @Override
    public Task findById(Integer id) {
        Task task = (Task) sessionFactory.getCurrentSession().createQuery("from Task where id = :id").setParameter("id", id).getSingleResult();
        return task;
    }

    @Transactional
    @Override
    public void delete(Task entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Transactional
    @Override
    public List<Task> findAll() {
        List<Task> tasks = (List<Task>) sessionFactory.getCurrentSession().createQuery("from Task").list();
        return tasks;
    }


    @Transactional
    @Override
    public List<Task> findAllByUser(Integer currentUserId) {
        List<Task> tasks = (List<Task>) sessionFactory.getCurrentSession().createQuery("from Task where user.id = :currentUserId").setParameter("currentUserId", currentUserId).list();
        return tasks;
    }

    @Override
    public void deleteAll() {
        List<Task> listUser = findAll();
        for (Task entity : listUser) {
            delete(entity);
        }
    }
}
