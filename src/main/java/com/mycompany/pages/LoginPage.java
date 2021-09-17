package com.mycompany.pages;

import com.mycompany.TMWebSession;
import com.mycompany.entity.enums.Role;
import com.mycompany.models.UserModel;
import com.mycompany.service.UserService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

public class LoginPage extends WebPage {

    @SpringBean
    private UserService userService;

    private Form form;
    private WebMarkupContainer divContainer;
    private TextField<Object> usernameTextField;
    private PasswordTextField passwordTextField;
    private List<UserModel> userModels;

    private String username;
    private String password;


    public LoginPage(PageParameters params) {
        form = new Form("formId");
        form.setOutputMarkupId(true);
        add(form);
        divContainer = new WebMarkupContainer("divId");
        divContainer.setOutputMarkupId(true);
        usernameTextField = new TextField<Object>("usernameId");
        usernameTextField.setDefaultModel(Model.of(""));
        usernameTextField.setOutputMarkupId(true);
        usernameTextField.setRequired(true);
        usernameTextField.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                username = (String) usernameTextField.getModelObject();

            }
        });
        divContainer.add(usernameTextField);
        passwordTextField = new PasswordTextField("passwordId");
        passwordTextField.setDefaultModel(Model.of(""));
        passwordTextField.setOutputMarkupId(true);
        passwordTextField.setRequired(true);
        passwordTextField.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                password = passwordTextField.getModelObject();
            }
        });
        divContainer.add(passwordTextField);

        AjaxSubmitLink ajaxSubmitLink = new AjaxSubmitLink("buttonId") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                TMWebSession session = TMWebSession.get();
                boolean isAuthenticated = session.authenticate(username, password);
                if (isAuthenticated) {
                    if (session.getRole() == Role.MANAGER) {
                        setResponsePage(ManagerPage.class);
                    }
                    else {
                        setResponsePage(EmployeePage.class);
                    }

                } else {
                    target.appendJavaScript("alert('invalid username or password!!!')");
                }
            }
        };
        ajaxSubmitLink.setOutputMarkupId(true);
        divContainer.add(ajaxSubmitLink);
        form.add(divContainer);
        add(form);
    }


}