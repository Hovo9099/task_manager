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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

public class TMWebSession extends AuthenticatedWebSession {

    @SpringBean
    private PasswordEncoder passwordEncoder;

    @SpringBean
    private UserDao userDao;

    @SpringBean
    private TaskDao taskDao;

    private Role role;
    private String currentUser;
    private Integer currentId;

    private HttpSession httpSession;

    public TMWebSession(Request request) {
        super(request);
        this.httpSession = ((HttpServletRequest) request.getContainerRequest()).getSession();
        Injector.get().inject(this);
    }

    @Override
    public Roles getRoles() {
        return new Roles();
    }

    @Override
    public boolean authenticate(String username, String password) {
        User user = userDao.getByUser(username);
        setRole(user.getRole());
        setCurrentId(user.getId());
        setCurrentUser(user.getUsername());

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            Authentication a = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            if(a.isAuthenticated()){
//                signIn(true);
//                SecurityContextHolder.getContext().setAuthentication(a);
//            httpSession.setAttribute(
//                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
//                    SecurityContextHolder.getContext());


            return true;
        }
        return false;

    }


//        return false;
//    }

    @Override
    public void signOut() {
        super.signOut();
        SecurityContextHolder.clearContext();
    }

    public static TMWebSession get() {
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


    public Integer getCurrentId() {
        return currentId;
    }

    public void setCurrentId(Integer id) {
        this.currentId = id;
    }
}
