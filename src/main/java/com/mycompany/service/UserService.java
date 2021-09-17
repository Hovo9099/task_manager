package com.mycompany.service;

import com.mycompany.entity.User;
import com.mycompany.models.UserModel;

import java.util.List;

public interface UserService {
    void save(UserModel userModel);

    List<UserModel> getUserModels();

    Boolean getUserLogin(String username, String password);

    UserModel getUserById(Integer id);

    void saveHasResume(UserModel userModel);
}
