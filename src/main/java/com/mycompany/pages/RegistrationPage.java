package com.mycompany.pages;

import com.mycompany.entity.enums.Role;
import com.mycompany.models.UserModel;
import com.mycompany.service.UserService;
import com.mycompany.service.UserServiceImpl;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;

import java.util.Arrays;
import java.util.List;

public class RegistrationPage extends WebPage {

    @SpringBean
    private UserService userService;

    private Form form;
    private WebMarkupContainer divContainer;
    private TextField<String> username;
    private TextField<String> password;
    private DropDownChoice<Role> selectRole;
    private List<Role> listRoles;

    public RegistrationPage() {
        form = new Form("formId");
        form.setOutputMarkupId(true);
        add(form);
        divContainer = new WebMarkupContainer("divId");
        divContainer.setOutputMarkupId(true);
        username = new TextField<String>("usernameId", Model.of(""));
        username.setOutputMarkupId(true);
//        username.add(new PatternValidator("^[A-Za-z][A-Za-z0-9_]{7,29}$"));
        divContainer.add(username);
        password = new TextField<String>("passwordId", Model.of(""));
        password.setOutputMarkupId(true);
//        password.add(new PatternValidator("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$"));
        divContainer.add(password);
        listRoles = Arrays.asList(Role.MANAGER, Role.EMPLOYEE);
        selectRole = new DropDownChoice<Role>("roleId", Model.of(Role.EMPLOYEE), listRoles, new IChoiceRenderer<Role>() {
            @Override
            public Object getDisplayValue(Role role) {
                return role.getName();
            }
        });
        selectRole.setOutputMarkupId(true);
        divContainer.add(selectRole);
        AjaxSubmitLink ajaxSubmitLink = new AjaxSubmitLink("buttonId") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                UserModel userModel = new UserModel();
                userModel.setUsername(username.getModelObject());
                userModel.setPassword(password.getModelObject());
                userModel.setRole(selectRole.getModelObject());
                userService.save(userModel);
                setResponsePage(ManagerPage.class);
            }
        };
        ajaxSubmitLink.setOutputMarkupId(true);
        divContainer.add(ajaxSubmitLink);
        form.add(divContainer);
        add(form);
    }
}
