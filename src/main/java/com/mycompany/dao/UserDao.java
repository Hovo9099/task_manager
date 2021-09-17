package com.mycompany.dao;

import com.mycompany.entity.User;
import com.mycompany.models.UserModel;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

public interface UserDao extends CustomDao<User> {
    User getByUser(String username);

}
