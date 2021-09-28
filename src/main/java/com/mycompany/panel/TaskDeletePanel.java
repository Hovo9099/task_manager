package com.mycompany.panel;

import com.mycompany.models.TaskModel;
import com.mycompany.service.TaskService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.List;

public abstract class TaskDeletePanel extends Panel implements Serializable {

    @SpringBean
    private TaskService taskService;



    public TaskDeletePanel(String id, TaskModel taskModel) {
        super(id);

        Form form = new Form("form");
        form.setOutputMarkupId(true);
        add(form);


        WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);

        Label label = new Label("label", "Are you sure want to delete task?");
        label.setOutputMarkupId(true);
        container.add(label);

        AjaxLink yesBtn = new AjaxLink<Object>("yesBtn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                taskService.deleteTask(taskModel.getId());
                onClose(target);
                refreshManagerPage(target);
            }
        };
        yesBtn.setOutputMarkupId(true);
        container.add(yesBtn);

        AjaxLink noBtn = new AjaxLink<Object>("noBtn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onClose(target);
            }
        };

        noBtn.setOutputMarkupId(true);
        container.add(noBtn);

        form.add(container);
    }



    public abstract void refreshManagerPage(AjaxRequestTarget target);

    public abstract void onClose(AjaxRequestTarget target);
}
