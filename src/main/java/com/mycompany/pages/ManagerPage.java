package com.mycompany.pages;

import com.mycompany.entity.enums.TaskStatus;
import com.mycompany.models.TaskModel;
import com.mycompany.models.UserModel;
import com.mycompany.panel.ManagerPanel;
import com.mycompany.service.TaskServiceImpl;
import com.mycompany.service.UserServiceImpl;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;
import java.util.List;

public class ManagerPage extends WebPage {

    @SpringBean
    private UserServiceImpl userServiceImpl;

    @SpringBean
    TaskServiceImpl taskServiceImpl;

    private Form form;
    private WebMarkupContainer divContainer;
    private DropDownChoice<UserModel> userSelect;
    private DropDownChoice<TaskModel> taskSelect;
    private List<TaskStatus> statusList;
    private ModalWindow modalWindow;
    private ListView<TaskModel> listView;
    private WebMarkupContainer listViewContainer;

    public ManagerPage() {
        listViewContainer = new WebMarkupContainer("listViewContainer");
        listViewContainer.setOutputMarkupPlaceholderTag(true);
        add(listViewContainer);
        form = new Form("formId");
        add(form);
        form.setOutputMarkupId(true);
        divContainer = new WebMarkupContainer("divId", Model.of(""));
        divContainer.setOutputMarkupId(true);
        form.add(divContainer);
        userSelect = new DropDownChoice<UserModel>("userSelect", new Model<UserModel>(),  userServiceImpl.getUserModels(), new IChoiceRenderer<UserModel>() {
            @Override
            public Object getDisplayValue(UserModel userModel) {
                return userModel.getUsername();
            }
        });
        userSelect.setOutputMarkupId(true);
        divContainer.add(userSelect);
        statusList = Arrays.asList(TaskStatus.values());
        taskSelect  = new DropDownChoice("taskStatusSelect", new Model<TaskStatus>(), statusList, new IChoiceRenderer<TaskStatus>() {
            @Override
            public Object getDisplayValue(TaskStatus taskStatus) {
                return taskStatus.getName();
            }
        });
        taskSelect.setOutputMarkupId(true);
        divContainer.add(taskSelect);
        initModalWindow();
        AjaxSubmitLink ajaxSubmitLink = new AjaxSubmitLink("filterButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {

            }
        };
        ajaxSubmitLink.setOutputMarkupId(true);
        divContainer.add(ajaxSubmitLink);

        AjaxSubmitLink createTaskBtn = new AjaxSubmitLink("createButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                if(!modalWindow.isShown()){
                    modalWindow.setContent(new ManagerPanel(modalWindow.getContentId()) {
                        @Override
                        public void refreshManagerPage(AjaxRequestTarget target) {
                           initializeListView(target, taskServiceImpl.getTaskModelList());
                        }
                    });
                    modalWindow.show(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                super.onError(target);
            }
        };

        initializeListView(null, taskServiceImpl.getTaskModelList());
        divContainer.add(createTaskBtn);
        form.add(divContainer);
    }


    private void initModalWindow() {
        modalWindow = new ModalWindow("modalWindow");
        modalWindow.showUnloadConfirmation(false);
        modalWindow.setInitialWidth(1100);
        modalWindow.setInitialHeight(600);
        modalWindow.setResizable(true);
        add(modalWindow);
    }

    private void initializeListView(AjaxRequestTarget target, List<TaskModel> taskModelList) {


        listView = new ListView<TaskModel>("listView", taskModelList) {
            @Override
            protected void populateItem(ListItem<TaskModel> listItem) {
                TaskModel taskModel = (TaskModel) listItem.getModelObject();
                listItem.add(new Label("l_name", taskModel.getName()));
                listItem.add(new Label("l_description", taskModel.getDescription()));
                listItem.add(new Label("l_user", taskModel.getUserModel().getUsername()));

                listItem.add(new AjaxLink<Void>("editButton") {
                    @Override
                    public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                        if(!modalWindow.isShown()){
                            modalWindow.setContent(new ManagerPanel(modalWindow.getContentId()) {
                                @Override
                                public void refreshManagerPage(AjaxRequestTarget target) {
                                    //TODO check
                                }
                            });
                            modalWindow.show(ajaxRequestTarget);
                        }
                    }
                });

                listItem.add(new AjaxLink<Void>("deleteButton") {
                    @Override
                    public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                      taskServiceImpl.deleteTask(taskModel.getId());
                        List<TaskModel> temp = taskServiceImpl.getTaskModelList();
                        temp.remove(taskModel);
                        initializeListView(target, temp);
                    }
                });
            }

        };
        listView.setOutputMarkupPlaceholderTag(true);
        listViewContainer.addOrReplace(listView);

        if (target != null) {
            target.add(listViewContainer);
        }

    }


}
