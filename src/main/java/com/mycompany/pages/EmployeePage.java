package com.mycompany.pages;

import com.mycompany.entity.Task;
import com.mycompany.entity.enums.TaskStatus;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.Model;

import java.util.Arrays;
import java.util.List;

public class EmployeePage extends WebPage {
    private Form form;
    private WebMarkupContainer divContainer;
    private List<TaskStatus> statusList;
    private DropDownChoice<TaskStatus> taskSelect;
    private DropDownChoice<Task> dateSelect;
    private AjaxSubmitLink ajaxSubmitLink;

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

    }
}
