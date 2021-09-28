package com.mycompany.panel;

import com.mycompany.TMWebSession;
import com.mycompany.entity.enums.EditEnum;
import com.mycompany.entity.enums.TaskStatus;
import com.mycompany.models.TaskModel;
import com.mycompany.models.UserModel;
import com.mycompany.service.TaskService;
import com.mycompany.service.UserService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public abstract class TaskEditPanel extends Panel implements Serializable {

    @SpringBean
    private TaskService taskService;

    @SpringBean
    private UserService userService;

    private Form form;
    private WebMarkupContainer divContainer;
    private TextField<String> taskName;
    private TextField<String> description;
    private DropDownChoice<UserModel> userSelect;
    private DropDownChoice<TaskStatus> statusSelect;
    private ModalWindow modalWindow;
    private List<TaskStatus> taskStatusList;
    private String changeTaskName;
    private String changeDescription;
    private TaskStatus selectedStatus;
    private UserModel selectedUserModel;
    private TaskModel currentTaskModel;


    public TaskEditPanel(String id, TaskModel taskModel1, EditEnum editEnum) {
        super(id);

        this.currentTaskModel = taskModel1;

        form = new Form("formId");
        form.setOutputMarkupId(true);
        add(form);
        divContainer = new WebMarkupContainer("divId");
        divContainer.setOutputMarkupId(true);
        String currentTaskName = taskModel1.getName() == null ? "" : taskModel1.getName();
        taskName = new TextField<String>("name", new Model<String>(currentTaskName));
        taskName.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                changeTaskName = (String) getComponent().getDefaultModelObject();
            }
        });
        taskName.setOutputMarkupId(true);
        divContainer.add(taskName);

        String currentDescription = taskModel1.getDescription() == null ? "" : taskModel1.getDescription();
        description = new TextField<String>("description", new Model<String>(currentDescription));
        description.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                changeDescription = (String) getComponent().getDefaultModelObject();
            }
        });
        description.setOutputMarkupId(true);
        divContainer.add(description);

        taskStatusList = Arrays.asList(TaskStatus.values());
        if (editEnum == EditEnum.EDIT_EMPLOYEE) {;
            taskStatusList = TaskStatus.getEmployeeStatuses();
        }

//        TaskStatus currentStatus = taskModel1.getStatus() == null ? null : taskModel1.getStatus();
        statusSelect = new DropDownChoice<TaskStatus>("status", new Model<TaskStatus>(taskModel1.getStatus()), taskStatusList, new IChoiceRenderer<TaskStatus>() {
            @Override
            public Object getDisplayValue(TaskStatus object) {
                return object == null ? null : object.getName();
            }
        });
        statusSelect.setNullValid(true);

        statusSelect.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedStatus = (TaskStatus) getComponent().getDefaultModelObject();
            }
        });
        statusSelect.setOutputMarkupId(true);

        if (editEnum == EditEnum.MANAGER_TASK_CREATION) {
            statusSelect.setVisible(false);
        }

        divContainer.add(statusSelect);

        UserModel currentUser = taskModel1.getUserModel() == null ? userService.getUserModels().get(0) : taskModel1.getUserModel();
        selectedUserModel = currentUser;
        userSelect = new DropDownChoice<UserModel>("user", new Model<UserModel>(currentUser), userService.getUserModels(), new IChoiceRenderer<UserModel>() {
            @Override
            public Object getDisplayValue(UserModel userModel) {
                return userModel.getUsername();
            }

            @Override
            public String getIdValue(UserModel object, int index) {
                return IChoiceRenderer.super.getIdValue(object, index);
            }
        });

        userSelect.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget ajaxRequestTarget) {
                selectedUserModel = (UserModel) getComponent().getDefaultModelObject();
            }
        });

        userSelect.setOutputMarkupId(true);
        divContainer.add(userSelect);

        if(editEnum == EditEnum.EDIT_EMPLOYEE) {
            taskName.setVisible(false);
            description.setVisible(false);
            userSelect.setVisible(false);
        }

        if (editEnum == EditEnum.EMPLOYEE_TASK_CREATION) {
            statusSelect.setVisible(false);
            userSelect.setVisible(false);
        }

        AjaxLink<Void> ajaxLink = new AjaxLink<Void>("button") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (editEnum == EditEnum.MANAGER_TASK_EDIT) {
                    TaskModel temp = new TaskModel();
                    temp.setId(currentTaskModel.getId());
                    temp.setName(taskName.getModelObject());
                    temp.setDescription(description.getModelObject());
                    temp.setStatus(statusSelect.getModelObject());
                    temp.setUserModel(userSelect.getModelObject());
                    taskService.update(temp);
                } else if (editEnum == EditEnum.MANAGER_TASK_CREATION){
                    TaskModel taskModel = new TaskModel();
                    taskModel.setName(taskName.getModelObject());
                    taskModel.setDescription(description.getModelObject());
                    taskModel.setStatus(TaskStatus.NEW_TASK);
                    taskModel.setUserModel(selectedUserModel);
                    taskService.save(taskModel);
                } else if(editEnum == EditEnum.EDIT_EMPLOYEE) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setId(currentTaskModel.getId());
                    taskModel.setName(taskName.getModelObject());
                    taskModel.setDescription(description.getModelObject());
                    taskModel.setStatus(statusSelect.getModelObject());
                    taskModel.setUserModel(userSelect.getModelObject());
                    taskService.update(taskModel);
                } else {
                    TMWebSession session = TMWebSession.get();
                    TaskModel taskModel = new TaskModel();
                    taskModel.setId(currentTaskModel.getId());
                    taskModel.setName(taskName.getModelObject());
                    taskModel.setDescription(description.getModelObject());
                    taskModel.setStatus(TaskStatus.NEW_TASK);
                    taskModel.setUserModel(userService.getUserById(session.getCurrentId()));
                    taskService.save(taskModel);
                }

                refreshManagerPage(target);
            }
        };

        divContainer.add(ajaxLink);
        form.add(divContainer);
    }




    public abstract void refreshManagerPage(AjaxRequestTarget target);


}
