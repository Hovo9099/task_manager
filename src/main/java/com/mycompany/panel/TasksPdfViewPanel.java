package com.mycompany.panel;

import com.mycompany.models.UserModel;
import com.mycompany.service.UserService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class TasksPdfViewPanel extends Panel implements Serializable {

    @SpringBean
    private UserService userService;

    public TasksPdfViewPanel(String id) {
        super(id);

        WebMarkupContainer pdfListViewContainer = new WebMarkupContainer("pdfListViewContainer");
        pdfListViewContainer.setOutputMarkupId(true);
        add(pdfListViewContainer);

        ListView<UserModel> pdfListView = new ListView<UserModel>("pdfListView", userService.getUserModels()) {
            @Override
            protected void populateItem(ListItem item) {

                UserModel currentUserModel = (UserModel) item.getModelObject();
                AjaxLink<Void> link = new AjaxLink<Void>("pdfName") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        Path path = Paths.get("C:", "data", String.valueOf(currentUserModel.getId()), currentUserModel.getPdfName());
                        readDocument(target, path.toString(), currentUserModel.getPdfName());
                    }
                };
                link.setOutputMarkupId(true);
                link.setMarkupId(currentUserModel.getId() + "f");
                if (!currentUserModel.getPdfName().equals("")) {//---------------
                    item.add(new Label("user", currentUserModel.getUsername()));
                    item.add(link);
                    link.add(new Label("name", currentUserModel.getPdfName()));
                } else {//----------------------
                    item.add(new Label("user", currentUserModel.getUsername()));
                    link.add(new Label("name", "this user has not pdf!!!!"));
                    item.add(link);
                }
            }
        };

        pdfListView.setOutputMarkupId(true);
        pdfListViewContainer.add(pdfListView);

    }

    public abstract void readDocument(AjaxRequestTarget target, String filePath, String fileName);


}
