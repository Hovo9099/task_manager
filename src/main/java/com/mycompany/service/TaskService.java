package com.mycompany.service;

import com.mycompany.entity.Task;
import com.mycompany.entity.User;
import com.mycompany.entity.enums.TaskStatus;
import com.mycompany.models.TaskModel;
import com.mycompany.models.UserModel;

import java.util.Date;
import java.util.List;

public interface TaskService {

    void save(TaskModel taskModel);

    List<TaskModel> getTaskModelList();

    void deleteTask(Integer id);

    User getUserEntity(UserModel userModel);

    List<TaskModel> getTasksByUser(String username);

    UserModel getUserModelToUser(User user);

    List<TaskModel> getAllTaskByUsersAndStatus(String username, TaskStatus status);

    List<TaskModel> getTaskCreationDates();

    List<TaskModel> getAllTaskByStatusAndCreationDates(TaskStatus status, Date creationDate, String username);

}
