package com.mycompany.service;

import com.mycompany.entity.User;
import com.mycompany.models.TaskModel;
import com.mycompany.models.UserModel;

import java.util.List;

public interface TaskService {

    void save(TaskModel taskModel);

    List<TaskModel> getTaskModelList();

    void deleteTask(Integer id);

    User getUserEntity(UserModel userModel);
}
