package com.mycompany;

import com.mycompany.dao.TaskDao;
import com.mycompany.dao.UserDao;
import com.mycompany.entity.User;
import com.mycompany.entity.enums.Role;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;

import java.security.Security;

public class TMWebSession extends AuthenticatedWebSession {

    @SpringBean
    private PasswordEncoder passwordEncoder;

    @SpringBean
    private UserDao userDao;

    @SpringBean
    private TaskDao taskDao;

    private Role role;

    private String currentUser;


    public TMWebSession(Request request) {
        super(request);
        Injector.get().inject(this);
    }

    @Override
    public Roles getRoles() {
        return new Roles();
    }

    @Override
    public boolean authenticate(String username, String password)  {
        User user = userDao.getByUser(username);
        setRole(user.getRole());
        setCurrentUser(user.getUsername());
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return true;
        }
        return false;
    }

    @Override
    public void signOut() {
        super.signOut();
        SecurityContextHolder.clearContext();
    }

    public static TMWebSession get(){
        return (TMWebSession) Session.get();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
}
