package com.mycompany.dao;

import com.mycompany.entity.Task;
import com.mycompany.entity.enums.TaskStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface TaskDao extends CustomDao<Task> {

    @Transactional
    List<Task> findAllByUser(Integer currentUserId);

    @Transactional
    List<Task> findAllTaskByUser(String username);

    @Transactional
    List<Task> getAllTaskByUsersAndStatus(String username, TaskStatus status);

    @Transactional
    List<Task> getAllTaskByStatusAndCreationDates(TaskStatus status, Date creationDate, String username);

}
