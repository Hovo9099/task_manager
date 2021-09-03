package com.mycompany.dao;

import com.mycompany.entity.Task;
import com.mycompany.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskDao extends CustomDao<Task> {

    @Transactional
    List<Task> findAllByUser(Integer currentUserId);

    @Transactional
    List<Task> findAllTaskByUser(String username);

}
