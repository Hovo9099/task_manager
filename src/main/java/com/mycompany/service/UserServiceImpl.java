package com.mycompany.service;

import com.mycompany.dao.UserDao;
import com.mycompany.entity.User;
import com.mycompany.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceImpl implements UserService, Serializable {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDao userDao;


    @Override
    public void save(UserModel userModel){
        User user = new User();
        user.setUsername(userModel.getUsername());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        user.setRole(userModel.getRole());
        userDao.persist(user);
    }

    @Override
    public List<UserModel> getUserModels() {
        List<UserModel> userModelList = new ArrayList<UserModel>();
        List<User> userList = userDao.findAll();
        for (User user: userList) {
            UserModel userModel = new UserModel();
            userModel.setId(user.getId());
            userModel.setUsername(user.getUsername());
            userModel.setPdfName(user.getPdfName());
            userModelList.add(userModel);
        }
        return userModelList;
    }

    @Override
    public Boolean getUserLogin(String username, String password) {
        User user = userDao.getByUser(username);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    @Override
    public UserModel getUserById(Integer id) {
        User user = userDao.findById(id);
        UserModel userModel = new UserModel();

        userModel.setId(user.getId());
        userModel.setUsername(user.getUsername());

        return userModel;
    }



    @Override
    public void saveHasResume(UserModel userModel) {
        User user = userDao.findById(userModel.getId());
        user.setHasResume(userModel.getHasResume());
        user.setPdfName(userModel.getPdfName());
        userDao.update(user);
    }



}
