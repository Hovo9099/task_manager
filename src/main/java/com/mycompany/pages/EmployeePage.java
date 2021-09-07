package com.mycompany.pages;

import com.mycompany.TMWebSession;
import com.mycompany.entity.Task;
import com.mycompany.entity.enums.TaskStatus;
import com.mycompany.models.TaskModel;
import com.mycompany.service.TaskService;
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

public class EmployeePage extends WebPage {

    @SpringBean
    private TaskService taskService;

    private Form form;
    private WebMarkupContainer divContainer;
    private List<TaskStatus> statusList;
    private DropDownChoice<TaskStatus> taskSelect;
    private DropDownChoice<Task> dateSelect;
    private AjaxSubmitLink ajaxSubmitLink;
    private WebMarkupContainer listViewContainer;
    private ListView<TaskModel> listView;
    private ModalWindow modalWindow;
    private List<TaskModel> taskModelList;
    private TMWebSession session;

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
        taskSelect.setOutputMarkupId(true);
        divContainer.add(taskSelect);
        dateSelect = new DropDownChoice<Task>("dateSelect");
        dateSelect.setOutputMarkupId(true);
        divContainer.add(dateSelect);
        ajaxSubmitLink = new AjaxSubmitLink("filterButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {

            }
        };
        ajaxSubmitLink.setOutputMarkupId(true);
        divContainer.add(ajaxSubmitLink);
        form.add(divContainer);

        listViewContainer = new WebMarkupContainer("listViewContainer");
        listViewContainer.setOutputMarkupId(true);

        session = TMWebSession.get();
        taskModelList = taskService.getTaskByUser(session.getCurrentUser());
        listView = new ListView<TaskModel>("listView", taskModelList) {
            @Override
            protected void populateItem(ListItem<TaskModel> item) {
                TaskModel taskModel = item.getModelObject();

                for (TaskModel taskModelItem: taskModelList) {
                    item.add(new Label("name", taskModelItem.getName()));
                    item.add(new Label("description", taskModelItem.getDescription()));
                    item.add(new Label("status", taskModelItem.getStatus().getName()));
                    item.add(new Label("user", taskModelItem.getUserModel().getUsername()));
                    item.add(new AjaxLink<Void>("editButton") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            System.out.println("AsD");
                        }
                    });
                }

            }
        };
        listView.setOutputMarkupPlaceholderTag(true);
        listViewContainer.add(listView);
        add(listViewContainer);

    }
}
