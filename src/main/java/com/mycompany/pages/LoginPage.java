package com.mycompany.pages;

import com.mycompany.models.UserModel;
import com.mycompany.service.UserServiceImpl;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

public class LoginPage extends WebPage {

    @SpringBean
    private UserServiceImpl userService;

    private Form form;
    private WebMarkupContainer divContainer;
    private TextField<Object> usernameTextField;
    private PasswordTextField passwordTextField;
    private List<UserModel> userModels;

    private String username;
    private String password;


    public LoginPage() {
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
                if (userService.getUserLogin(username, password) == true) {
                    setResponsePage(ManagerPage.class);
                } else {

                }
            }
        };
        ajaxSubmitLink.setOutputMarkupId(true);
        divContainer.add(ajaxSubmitLink);

        AjaxSubmitLink ajaxSubmitLink1 = new AjaxSubmitLink("registerButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                setResponsePage(RegistrationPage.class);
            }
        };
        ajaxSubmitLink1.setOutputMarkupId(true);
        divContainer.add(ajaxSubmitLink1);
        form.add(divContainer);
        add(form);
    }


}