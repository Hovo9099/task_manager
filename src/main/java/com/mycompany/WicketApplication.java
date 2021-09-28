package com.mycompany;

import com.mycompany.pages.EmployeePage;
import com.mycompany.pages.LoginPage;
import com.mycompany.pages.ManagerPage;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.RoleAuthorizationStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.string.StringValue;

import java.util.Set;

public class WicketApplication extends AuthenticatedWebApplication {

    @Override
    protected void init() {
        super.init();
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));
        getSecuritySettings().setAuthorizationStrategy(new AnnotationsRoleAuthorizationStrategy(this));
        mountPages();

        getSecuritySettings().setAuthorizationStrategy(new RoleAuthorizationStrategy(new IRoleCheckingStrategy() {
            @Override
            public boolean hasAnyRole(Roles roles) {
                return false;
            }
        }));

        getSecuritySettings().setUnauthorizedComponentInstantiationListener(new IUnauthorizedComponentInstantiationListener() {
            @Override
            public void onUnauthorizedInstantiation(Component component) {
                if (component instanceof Page) {
                    if (!AbstractAuthenticatedWebSession.get().isSignedIn()) {
                        restartResponseAtSignInPage((Page) component);
                    } else {
                        onUnauthorizedPage((Page) component);
                    }
                } else  {
                    throw new UnauthorizedInstantiationException(component.getClass());
                }
            }
        });
    }

    private void restartResponseAtSignInPage(Page page) {
        Set<String> parameterNames = RequestCycle.get().getRequest().getQueryParameters().getParameterNames();
        PageParameters params = new PageParameters();
        if (parameterNames != null) {
            for (String param : parameterNames) {
                StringValue paramValue = RequestCycle.get().getRequest().getQueryParameters().getParameterValue(param);
                params.add(param, paramValue.toString());
            }
            throw new RestartResponseAtInterceptPageException(new LoginPage(params));
        }else{
            throw new RestartResponseAtInterceptPageException(new LoginPage(new PageParameters()));
        }
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return TMWebSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    protected void mountPages() {
        mountPage("login", LoginPage.class);
        mountPage("employee", EmployeePage.class);
        mountPage("manager", ManagerPage.class);
    }

}
