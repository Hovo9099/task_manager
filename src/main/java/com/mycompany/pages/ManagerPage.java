package com.mycompany.pages;

import com.mycompany.entity.enums.EditEnum;
import com.mycompany.entity.enums.TaskStatus;
import com.mycompany.models.TaskModel;
import com.mycompany.models.UserModel;
import com.mycompany.panel.TaskDeletePanel;
import com.mycompany.panel.TaskEditPanel;
import com.mycompany.panel.TasksPdfViewPanel;
import com.mycompany.service.TaskService;
import com.mycompany.service.UserService;
import org.apache.wicket.AttributeModifier;
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
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class ManagerPage extends WebPage {

    @SpringBean
    private UserService userService;

    @SpringBean
    private TaskService taskService;

    private Form form;
    private WebMarkupContainer divContainer;
    private DropDownChoice<UserModel> userSelect;
    private DropDownChoice<TaskModel> taskStatusSelect;
    private List<TaskStatus> statusList;
    private ModalWindow modalWindow;
    private ListView<TaskModel> listView;
    private WebMarkupContainer listViewContainer;
    private TaskStatus selectedStatus;
    private UserModel selectedUser;
    private WebMarkupContainer iFrame;


    public ManagerPage() {

        iFrame = new WebMarkupContainer("iFrame");
        iFrame.setOutputMarkupPlaceholderTag(true);
        addOrReplace(iFrame);

        initModalWindow();

        listViewContainer = new WebMarkupContainer("listViewContainer");
        listViewContainer.setOutputMarkupPlaceholderTag(true);
        add(listViewContainer);
        form = new Form("formId");
        form.setOutputMarkupId(true);
        add(form);

        divContainer = new WebMarkupContainer("divId", Model.of(""));
        divContainer.setOutputMarkupId(true);
        form.add(divContainer);
        userSelect = new DropDownChoice<UserModel>("userSelect", new Model<UserModel>(),  userService.getUserModels(), new IChoiceRenderer<UserModel>() {
            @Override
            public Object getDisplayValue(UserModel userModel) {
                return userModel.getUsername();
            }
        });
        userSelect.setOutputMarkupId(true);

        userSelect.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedUser = (UserModel) getComponent().getDefaultModelObject();
            }
        });

        divContainer.add(userSelect);
        statusList = Arrays.asList(TaskStatus.values());
        taskStatusSelect = new DropDownChoice("taskStatusSelect", new Model<TaskStatus>(), statusList, new IChoiceRenderer<TaskStatus>() {
            @Override
            public Object getDisplayValue(TaskStatus taskStatus) {
                return taskStatus.getName();
            }
        });
        taskStatusSelect.setOutputMarkupId(true);

        taskStatusSelect.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                selectedStatus = (TaskStatus) getComponent().getDefaultModelObject();
            }
        });

        divContainer.add(taskStatusSelect);




        AjaxSubmitLink ajaxSubmitLink = new AjaxSubmitLink("filterButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                if (selectedUser != null && selectedStatus != null) {
                    List<TaskModel> taskModelList = taskService.getAllTaskByUsersAndStatus(selectedUser.getUsername(), selectedStatus);
                    initializeListView(target, taskModelList);
                } else {
                    target.appendJavaScript("alert('check user or status!!!')");
                }

            }
        };
        ajaxSubmitLink.setOutputMarkupId(true);
        divContainer.add(ajaxSubmitLink);

        AjaxSubmitLink createTaskBtn = new AjaxSubmitLink("createButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                if(!modalWindow.isShown()) {
                    TaskModel taskModel = new TaskModel();
                    taskModel.setStatus(TaskStatus.NEW_TASK);
                    modalWindow.setContent(new TaskEditPanel(modalWindow.getContentId(), taskModel,EditEnum.MANAGER_TASK_CREATION) {
                        @Override
                        public void refreshManagerPage(AjaxRequestTarget target) {
                           initializeListView(target, taskService.getTaskModelList());
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

        initializeListView(null, taskService.getTaskModelList());
        divContainer.add(createTaskBtn);

        AjaxSubmitLink registerButton = new AjaxSubmitLink("registerButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                setResponsePage(RegistrationPage.class);
            }
        };

        registerButton.setOutputMarkupId(true);
        divContainer.add(registerButton);

        AjaxSubmitLink logout = new AjaxSubmitLink("logout") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                AuthenticatedWebSession.get().signOut();
                getSession().invalidate();
                setResponsePage(getApplication().getHomePage());
            }
        };
        logout.setOutputMarkupId(true);
        divContainer.add(logout);
        form.add(divContainer);

        add(new TasksPdfViewPanel("panelPdfId") {
            @Override
            public void readDocument(AjaxRequestTarget target, String filePath, String fileName) {
                File headingFile = new File(filePath);
                ResourceReference resourceReference = new ResourceReference(this.getClass(), fileName) {

                    private static final long serialVersionUID = -6494747414399398897L;

                    @Override
                    public IResource getResource() {

                        byte[] byteArray = null;
                        try {
                            byteArray = Files.readAllBytes(headingFile.toPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return new ByteArrayResource("application/pdf", byteArray);
                    }

                };

                if (resourceReference.canBeRegistered()) {
                    getApplication().getResourceReferenceRegistry().registerResourceReference(resourceReference);
                }

                String urlForResourceReference = ((String) urlFor(resourceReference, null)).concat("?nocache=" + UUID.randomUUID().toString());

                iFrame.add(new AttributeModifier("src", urlForResourceReference) );
                target.add(iFrame);
            }
        });



    }

    private void initModalWindow() {
        modalWindow = new ModalWindow("modalWindow");
        modalWindow.setOutputMarkupId(true);
        modalWindow.showUnloadConfirmation(false);
        modalWindow.setInitialWidth(300);
        modalWindow.setInitialHeight(100);
        modalWindow.setResizable(true);
        add(modalWindow);

    }

    private void initializeListView(AjaxRequestTarget target, List<TaskModel> taskModelList) {

        listView = new ListView<TaskModel>("listView", taskModelList) {
            @Override
            protected void populateItem(ListItem<TaskModel> listItem) {
                int index = listItem.getIndex();
                TaskModel currentTaskModel = listItem.getModelObject();
                listItem.add(new Label("name", currentTaskModel.getName()));
                listItem.add(new Label("description", currentTaskModel.getDescription()));
                listItem.add(new Label("status", currentTaskModel.getStatus().getName()));
                listItem.add(new Label("user", currentTaskModel.getUserModel().getUsername()));

                AjaxLink editButton = new  AjaxLink<Void>("editButton") {
                    @Override
                    public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                        if(!modalWindow.isShown()) {
                            modalWindow.setContent(new TaskEditPanel(modalWindow.getContentId(), currentTaskModel, EditEnum.MANAGER_TASK_EDIT) {
                                @Override
                                public void refreshManagerPage(AjaxRequestTarget target2) {
                                    initializeListView(target2, taskService.getTaskModelList());
                                    modalWindow.close(target2);
                                }
                            });
                            modalWindow.show(ajaxRequestTarget);
                        }
                    }
                };
                editButton.setOutputMarkupId(true).setMarkupId("edit" + index);
                listItem.add(editButton);

               AjaxLink deleteButton = new AjaxLink<Void>("deleteButton") {
                    @Override
                    public void onClick(AjaxRequestTarget ajaxRequestTarget) {


                        if(!modalWindow.isShown()) {
                            modalWindow.setContent(new TaskDeletePanel(modalWindow.getContentId(), currentTaskModel) {
                                @Override
                                public void refreshManagerPage(AjaxRequestTarget target) {
                                    initializeListView(target, taskService.getTaskModelList());
                                }

                                @Override
                                public void onClose( AjaxRequestTarget target) {

                                    modalWindow.close(target);

                                }
                            });

                            modalWindow.show(ajaxRequestTarget);
                        }
                    }
                };
               deleteButton.setOutputMarkupId(true).setMarkupId("delete_" + index);
               listItem.setOutputMarkupId(true).setMarkupId("row_" + index);
               listItem.add(deleteButton);

            }
        };

        listView.setOutputMarkupId(true);
        listViewContainer.addOrReplace(listView);

        if (target != null) {
            target.add(listViewContainer);
        }

    }


}
