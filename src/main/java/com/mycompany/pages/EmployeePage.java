package com.mycompany.pages;

import com.mycompany.TMWebSession;
import com.mycompany.entity.enums.EditEnum;
import com.mycompany.entity.enums.TaskStatus;
import com.mycompany.models.TaskModel;
import com.mycompany.models.UserModel;
import com.mycompany.panel.TaskEditPanel;
import com.mycompany.service.TaskService;
import com.mycompany.service.UserService;
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
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class EmployeePage extends WebPage {

    @SpringBean
    private TaskService taskService;

    @SpringBean
    private UserService userService;

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
    private UserModel userModel;

    private FileUploadField fileUpload;
    private String uploadFolder = "C:\\data";

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

        AjaxSubmitLink createTaskBtn = new AjaxSubmitLink("createTaskBtn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                if (!modalWindow.isShown()) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setStatus(TaskStatus.NEW_TASK);
                    modalWindow.setContent(new TaskEditPanel(modalWindow.getContentId(), taskModel, EditEnum.EMPLOYEE_TASK_CREATION) {
                        @Override
                        public void refreshManagerPage(AjaxRequestTarget target) {
                            initializeListView(target, taskService.getTasksByUser(session.getCurrentUser()));
                        }
                    });
                    modalWindow.show(target);
                }
            }
        };

        createTaskBtn.setOutputMarkupId(true);
        divContainer.add(createTaskBtn);

        AjaxSubmitLink logout = new AjaxSubmitLink("logout") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                getSession().invalidate();
                AuthenticatedWebSession.get().signOut();
                setResponsePage(getApplication().getHomePage());
            }
        };
        initModalWindow();
        logout.setOutputMarkupId(true);
        divContainer.add(logout);
        form.add(divContainer);

        listViewContainer = new WebMarkupContainer("listViewContainer");
        listViewContainer.setOutputMarkupId(true);

        session = TMWebSession.get();
        initializeListView(null, taskService.getTasksByUser(session.getCurrentUser()));

        add(new FeedbackPanel("feedback"));
        Form form = new Form("formUpload");
        add(form);
           AjaxSubmitLink uploadBtn = new AjaxSubmitLink("uploadBtn") {
               @Override
               protected void onSubmit(AjaxRequestTarget target) {
                   final FileUpload uploadedFile = fileUpload.getFileUpload();
                   if (uploadedFile != null) {
                       Path path = Paths.get("C:", "data", String.valueOf(session.getCurrentId()));
                       File folder = new File(path.toUri());

                       if (!folder.exists()) {
                           folder.mkdirs();
                       }

                       try {
                           File file = new File(String.valueOf(folder.toString()), uploadedFile.getClientFileName());
                           if(uploadedFile.getContentType().equals("application/pdf")) {

                               if (folder.listFiles() != null && folder.listFiles().length != 0) {
                                   File[] listFiles = folder.listFiles();
                                   for(File fileItem : listFiles) {
                                       fileItem.delete();
                                   }
//                                   userModel = new UserModel();
//                                   userModel.setId(session.getCurrentId());
//                                   userModel.setHasResume(false);
//                                   userModel.setPdfName(uploadedFile.getClientFileName());
//                                   userService.saveHasResume(userModel);
                               }

                               folder.createNewFile();
//                             uploadedFile.writeTo(newFile);
                               byte[] bytes = uploadedFile.getBytes();
                               OutputStream os = new FileOutputStream(file);
                               os.write(bytes);
                               os.close();
                               userModel = new UserModel();
                               userModel.setId(session.getCurrentId());
                               userModel.setHasResume(true);
                               userModel.setPdfName(uploadedFile.getClientFileName());
                               userService.saveHasResume(userModel);
                           } else {
                               target.appendJavaScript("alert('file is not pdf!')");
                           }
                       } catch (Exception e) {
                           throw new IllegalStateException("error");
                       }

                   }

               }
           };
           uploadBtn.setOutputMarkupId(true);
           form.add(uploadBtn);


        form.setMultiPart(true);
        form.setMaxSize(Bytes.kilobytes(50000));
        form.add(fileUpload = new FileUploadField("fileUpload"));
        add(form);

    }

    private void initializeListView(AjaxRequestTarget target, List<TaskModel> taskModelList) {
        listView = new ListView<TaskModel>("listView", taskModelList) {
            @Override
            protected void populateItem(ListItem<TaskModel> item) {
                int index = item.getIndex();
                TaskModel taskModelItem = item.getModelObject();
                item.add(new Label("index", item.getIndex() + 1));
                item.add(new Label("name", taskModelItem.getName()));
                item.add(new Label("description", taskModelItem.getDescription()));
                item.add(new Label("status", taskModelItem.getStatus().getName()));
                item.add(new Label("user", taskModelItem.getUserModel().getUsername()));
                AjaxLink<Void> editButton = new AjaxLink<Void>("editButton") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if(!modalWindow.isShown()) {
                            modalWindow.setContent(new TaskEditPanel(modalWindow.getContentId(), taskModelItem, EditEnum.EDIT_EMPLOYEE) {
                                @Override
                                public void refreshManagerPage(AjaxRequestTarget target2) {

                                    initializeListView(target2, taskService.getTasksByUser(session.getCurrentUser()));
                                    modalWindow.close(target2);
                                }
                            });
                            modalWindow.show(target);
                        }
                    }
                };
                editButton.setOutputMarkupId(true);
                item.add(editButton);
                item.setOutputMarkupId(true).setMarkupId("row_" + index);

            }
        };
        listView.setOutputMarkupId(true);
        listViewContainer.addOrReplace(listView);
        add(listViewContainer);

    }
    private void initModalWindow() {
        modalWindow = new ModalWindow("modalWindow");
        modalWindow.showUnloadConfirmation(false);
        modalWindow.setInitialWidth(1100);
        modalWindow.setInitialHeight(600);
        modalWindow.setResizable(true);
        add(modalWindow);
    }
}
