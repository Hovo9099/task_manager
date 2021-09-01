package com.mycompany;

import com.mycompany.pages.LoginPage;
import com.mycompany.pages.RegistrationPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;

public class HomePage extends WebPage {

    public HomePage() {
        AjaxLink<Object> signUp = new AjaxLink<Object>("buttonLogId") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                setResponsePage(LoginPage.class);
            }
        };
        add(signUp);
        AjaxLink<Object> register = new AjaxLink<Object>("buttonRegId") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                setResponsePage(RegistrationPage.class);
            }
        };
        add(register);
    }
}
