package com.mycompany.service;

import com.mycompany.dao.TaskDao;
import com.mycompany.dao.UserDao;
import com.mycompany.entity.Task;
import com.mycompany.entity.User;
import com.mycompany.entity.enums.TaskStatus;
import com.mycompany.models.TaskModel;
import com.mycompany.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class TaskServiceImpl implements TaskService, Serializable {

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private UserDao userDao;


    @Override
    public void save(TaskModel taskModel) {
        Task task = new Task();
        task.setName(taskModel.getName());
        task.setDescription(taskModel.getDescription());
        task.setStatus(taskModel.getStatus());
        task.setUser(getUserEntity(taskModel.getUserModel()));
        taskDao.persist(task);
    }

    @Override
    public List<TaskModel> getTaskModelList() {

        List<Task> taskEntityList = taskDao.findAll();
        List<TaskModel> taskModels = new ArrayList<>();
        for (Task item : taskEntityList) {
            TaskModel taskModel = new TaskModel();
            UserModel userModel = new UserModel();

            User user = item.getUser();
            userModel.setId(user.getId());
            userModel.setUsername(user.getUsername());

            taskModel.setId(item.getId());
            taskModel.setName(item.getName());
            taskModel.setDescription(item.getDescription());
            taskModel.setStatus(item.getStatus());
            taskModel.setUserModel(userModel);
            taskModels.add(taskModel);
        }
        return taskModels;
    }


    @Override
    public void deleteTask(Integer id) {
        taskDao.delete(taskDao.findById(id));
    }


    @Override
    public User getUserEntity(UserModel userModel) {
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

    @Override
    public UserModel getUserModelToUser(User user) {

        UserModel userModel = new UserModel();
        userModel.setId(user.getId());
        userModel.setUsername(user.getUsername());
        return userModel;
    }

    @Override
    public List<TaskModel> getAllTaskByUsersAndStatus(String username, TaskStatus status) {
        List<Task> taskList = taskDao.getAllTaskByUsersAndStatus(username, status);
        return changeEntityToModel(taskList);
    }

    @Override
    public List<TaskModel> getTaskCreationDates() {
        List<Task> taskList = taskDao.findAll();
        List<TaskModel> taskModelList = new ArrayList<>();
        for (Task item : taskList) {
            TaskModel taskModel = new TaskModel();
            taskModel.setId(item.getId());
            taskModel.setCreationDate(item.getCreationDate());
            taskModelList.add(taskModel);
        }
        return taskModelList;
    }

    @Override
    public List<TaskModel> getAllTaskByStatusAndCreationDates(TaskStatus status, Date creationDate, String username) {
        List<Task> taskList = taskDao.getAllTaskByStatusAndCreationDates(status, creationDate, username);
        return changeEntityToModel(taskList);
    }

    @Override
    public void update(TaskModel taskModel) {
        Task task = taskDao.findById(taskModel.getId());
        task.setId(taskModel.getId());
        task.setName(taskModel.getName());
        task.setDescription(taskModel.getDescription());
        task.setStatus(taskModel.getStatus());
        task.setUser(getUserEntity(taskModel.getUserModel()));
        taskDao.update(task);
    }

    @Override
    public List<TaskModel> getTasksByUser(String username) {
        List<Task> tasksList = taskDao.findAllTaskByUser(username);
        return changeEntityToModel(tasksList);
    }

    private List<TaskModel> changeEntityToModel(List<Task> taskList) {
        List<TaskModel> taskModelList = new ArrayList<>();
        for (Task item: taskList) {
            TaskModel taskModel = new TaskModel();
            taskModel.setId(item.getId());
            taskModel.setName(item.getName());
            taskModel.setDescription(item.getDescription());
            taskModel.setStatus(item.getStatus());
            taskModel.setUserModel(getUserModelToUser(item.getUser()));
            taskModelList.add(taskModel);
        }
        return taskModelList;
    }


}
