package com.mycompany.service;

import com.mycompany.dao.TaskDao;
import com.mycompany.dao.UserDao;
import com.mycompany.entity.Task;
import com.mycompany.entity.User;
import com.mycompany.models.TaskModel;
import com.mycompany.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TaskService {

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private UserDao userDao;

    @Transactional
    public void save(TaskModel taskModel) {
        Task task = new Task();
        task.setName(taskModel.getName());
        task.setDescription(taskModel.getDescription());
        task.setStatus(taskModel.getStatus());
        task.setUser(getUserEntity(taskModel.getUserModel()));
        taskDao.persist(task);
    }

    private User getUserEntity(UserModel userModel) {
        User user;

        if (userModel.getId() != null) {
            user = userDao.findById(userModel.getId());
        }
        else {
            user = new User();
            user.setUsername(userModel.getUsername());
        }
        return user;
    }
}
