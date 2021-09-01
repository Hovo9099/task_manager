package com.mycompany.service;

import com.mycompany.dao.UserDao;
import com.mycompany.entity.User;
import com.mycompany.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDao userDao;

    @Transactional
    public void save(UserModel userModel){
        User user = new User();
        user.setUsername(userModel.getUsername());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        user.setRoles(userModel.getRole());
        userDao.persist(user);
    }


    public List<UserModel> getUserModels() {
        List<UserModel> userModelList = new ArrayList<UserModel>();
        List<User> userList = userDao.findAll();
        for (User user: userList) {
            UserModel userModel = new UserModel();
            userModel.setId(user.getId());
            userModel.setUsername(user.getUsername());
            userModelList.add(userModel);
        }
        return userModelList;
    }
}
