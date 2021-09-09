package com.mycompany.dao;

import com.mycompany.entity.Task;
import com.mycompany.entity.enums.TaskStatus;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
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

    @Override
    public Task findById(Integer id) {
        Task task = (Task) sessionFactory.getCurrentSession().createQuery("from Task where id = :id").setParameter("id", id).getSingleResult();
        return task;
    }

    @Override
    public void delete(Task entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public List<Task> findAll() {
        List<Task> tasks = (List<Task>) sessionFactory.getCurrentSession().createQuery("from Task").list();
        return tasks;
    }

    @Override
    public List<Task> findAllByUser(Integer currentUserId) {
        List<Task> tasks = (List<Task>) sessionFactory.getCurrentSession().createQuery("from Task where user.id = :currentUserId").setParameter("currentUserId", currentUserId).list();
        return tasks;
    }

    @Override
    public List<Task> findAllTaskByUser(String username) {
        List<Task> tasks = (List<Task>) sessionFactory.getCurrentSession().createQuery("from Task t where t.user.username = :username").setParameter("username", username).list();
        return tasks;
    }

    @Override
    public List<Task> getAllTaskByUsersAndStatus(String username, TaskStatus status) {
        List<Task> tasks = (List<Task>) sessionFactory.getCurrentSession().createQuery("from Task t where t.user.username = :username and t.status = :status").setParameter("username", username).setParameter("status", status).list();
        return tasks;
    }

    @Override
    public List<Task> getAllTaskByStatusAndCreationDates(TaskStatus status, Date creationDate, String username) {
        List<Task> tasks = (List<Task>) sessionFactory.getCurrentSession().createQuery("from Task t where t.status = :status and  t.creationDate = :creationDate and t.user.username = :username").setParameter("status", status).setParameter("creationDate", creationDate).setParameter("username", username).list();
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
