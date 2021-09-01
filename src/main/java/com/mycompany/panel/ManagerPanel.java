package com.mycompany.panel;

import com.mycompany.entity.enums.TaskStatus;
import com.mycompany.models.TaskModel;
import com.mycompany.models.UserModel;
import com.mycompany.service.TaskServiceImpl;
import com.mycompany.service.UserServiceImpl;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

public abstract class ManagerPanel extends Panel {

    @SpringBean
    private TaskServiceImpl taskServiceImpl;

    @SpringBean
    private UserServiceImpl userServiceImpl;

    private Form form;
    private WebMarkupContainer divContainer;
    private TextField<String> taskName;
    private TextArea<String> description;
    private List<UserModel> userList;
    private DropDownChoice<UserModel> userSelect;
    private ModalWindow modalWindow;
    private UserModel selectedModel;


    public ManagerPanel(String id) {
        super(id);

        form = new Form("formId");
        form.setOutputMarkupId(true);
        add(form);
        divContainer = new WebMarkupContainer("divId");
        divContainer.setOutputMarkupId(true);
        taskName = new TextField<String>("name", Model.of(""));
        taskName.setOutputMarkupId(true);
        divContainer.add(taskName);
        description = new TextArea<String>("description", Model.of(""));
        description.setOutputMarkupId(true);
        divContainer.add(description);
        userSelect = new DropDownChoice<UserModel>("user", new Model<UserModel>(), userServiceImpl.getUserModels(), new IChoiceRenderer<UserModel>() {
            @Override
            public Object getDisplayValue(UserModel userModel) {
                return userModel.getUsername();
            }
        });

        userSelect.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                selectedModel = (UserModel) getComponent().getDefaultModelObject();
            }
        });

        userSelect.setOutputMarkupId(true);
        divContainer.add(userSelect);
        AjaxSubmitLink ajaxSubmitLink = new AjaxSubmitLink("button") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                TaskModel taskModel = new TaskModel();
                taskModel.setName(taskName.getModelObject());
                taskModel.setDescription(description.getModelObject());
                taskModel.setStatus(TaskStatus.NEW_TASK);
                taskModel.setUserModel(selectedModel);
                taskServiceImpl.save(taskModel);
                onClose(target);
                refreshManagerPage(target);
            }
        };

        divContainer.add(ajaxSubmitLink);
        form.add(divContainer);
    }


    private void onClose(IPartialPageRequestHandler target) {
        modalWindow = new ModalWindow("modalWindow");
        modalWindow.close(target);
        add(modalWindow);
    }

    public abstract void refreshManagerPage(AjaxRequestTarget target);
}
