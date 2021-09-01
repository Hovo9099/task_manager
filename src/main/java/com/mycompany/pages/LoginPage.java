package com.mycompany.pages;

import com.mycompany.models.UserModel;
import com.mycompany.service.TaskServiceImpl;
import com.mycompany.service.UserServiceImpl;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

public class LoginPage extends WebPage {

    @SpringBean
    private UserServiceImpl userService;

    private Form form;
    private WebMarkupContainer divContainer;
    private TextField<Object> username;
    private TextField<Object> password;
    private List<UserModel> userModels;

    public LoginPage() {
        form = new Form("formId");
        form.setOutputMarkupId(true);
        add(form);
        divContainer = new WebMarkupContainer("divId");
        divContainer.setOutputMarkupId(true);
        username = new TextField<Object>("usernameId");
        username.setDefaultModel(Model.of(""));
        username.setOutputMarkupId(true);
        divContainer.add(username);
        password = new TextField<Object>("passwordId");
        password.setDefaultModel(Model.of(""));
        password.setOutputMarkupId(true);
        divContainer.add(password);

        AjaxSubmitLink ajaxSubmitLink = new AjaxSubmitLink("buttonId") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                userModels = userService.getUserModels();
                for (UserModel userModel : userModels) {
                    if (username.getModelObject().equals(userModel.getUsername()) && password.getModelObject().equals(userModel.getPassword())) {

                    }
                }
            }
        };
        ajaxSubmitLink.setOutputMarkupId(true);
        divContainer.add(ajaxSubmitLink);
        form.add(divContainer);
        add(form);
    }


}