package com.mycompany.pages;

import com.mycompany.TMWebSession;
import com.mycompany.entity.Task;
import com.mycompany.entity.enums.TaskStatus;
import com.mycompany.models.TaskModel;
import com.mycompany.service.TaskService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
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
import org.hibernate.annotations.Target;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EmployeePage extends WebPage {

    @SpringBean
    private TaskService taskService;

    private Form form;
    private WebMarkupContainer divContainer;
    private List<TaskStatus> statusList;
    private DropDownChoice<TaskStatus> taskSelect;
    private DropDownChoice<TaskModel> dateSelect;
    private List<TaskModel> datesList;
    private AjaxLink<Void> ajaxLink;
    private WebMarkupContainer listViewContainer;
    private ListView<TaskModel> listView;
    private ModalWindow modalWindow;
    private TMWebSession session;
    private TaskStatus changeStatus;
    private TaskModel changeDate;

    public EmployeePage() {
        form = new Form("formId");
        form.setOutputMarkupId(true);
        add(form);
        divContainer = new WebMarkupContainer("divId");
        divContainer.setOutputMarkupId(true);
        statusList = Arrays.asList(TaskStatus.values());
        taskSelect = new DropDownChoice<TaskStatus>("taskStatusSelect", new Model<TaskStatus>(), statusList, new IChoiceRenderer<TaskStatus>() {
            @Override
            public Object getDisplayValue(TaskStatus taskStatus) {
                return taskStatus.getName();
            }
        });

        taskSelect.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
               changeStatus = (TaskStatus) getComponent().getDefaultModelObject();
            }
        });
        taskSelect.setOutputMarkupId(true);
        divContainer.add(taskSelect);

        datesList = taskService.getTaskCreationDates();
        dateSelect = new DropDownChoice<TaskModel>("dateSelect", new Model<TaskModel>(), datesList, new IChoiceRenderer<TaskModel>() {
            @Override
            public Object getDisplayValue(TaskModel object) {
                return object.getCreationDate();
            }
        });

        dateSelect.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                changeDate = (TaskModel) getComponent().getDefaultModelObject();
            }
        });

        dateSelect.setOutputMarkupId(true);
        divContainer.add(dateSelect);
        ajaxLink = new AjaxLink<Void>("filterButton") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (changeStatus != null && changeDate != null) {
                    initializeListView(target, taskService.getAllTaskByStatusAndCreationDates(changeStatus, changeDate.getCreationDate(), session.getCurrentUser()));

                } else {
                    target.appendJavaScript("alert('check status or creation date!!!')");
                }
            }
        };
        ajaxLink.setOutputMarkupId(true);
        divContainer.add(ajaxLink);

        AjaxSubmitLink logout = new AjaxSubmitLink("logout") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                getSession().invalidate();
                AuthenticatedWebSession.get().signOut();
                setResponsePage(getApplication().getHomePage());
            }
        };
        logout.setOutputMarkupId(true);
        divContainer.add(logout);
        form.add(divContainer);

        listViewContainer = new WebMarkupContainer("listViewContainer");
        listViewContainer.setOutputMarkupId(true);

        session = TMWebSession.get();
        initializeListView(null, taskService.getTasksByUser(session.getCurrentUser()));

    }

    private void initializeListView(AjaxRequestTarget target, List<TaskModel> taskModelList) {
        listView = new ListView<TaskModel>("listView", taskModelList) {
            @Override
            protected void populateItem(ListItem<TaskModel> item) {
                TaskModel taskModelItem = item.getModelObject();
                item.add(new Label("index", item.getIndex() + 1));
                item.add(new Label("name", taskModelItem.getName()));
                item.add(new Label("description", taskModelItem.getDescription()));
                item.add(new Label("status", taskModelItem.getStatus().getName()));
                item.add(new Label("user", taskModelItem.getUserModel().getUsername()));
                item.add(new AjaxLink<Void>("editButton") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {

                    }
                });


            }
        };
        listView.setOutputMarkupPlaceholderTag(true);
        listViewContainer.addOrReplace(listView);
        add(listViewContainer);

    }
}
