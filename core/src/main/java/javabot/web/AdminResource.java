package javabot.web;

import com.google.inject.Injector;
import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;
import javabot.model.Admin;
import javabot.web.auth.Restricted;
import javabot.web.model.Authority;
import javabot.web.views.AdminIndexView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/admin")
public class AdminResource {
    @Inject
    private Injector injector;

    @GET
    public View index(@Context HttpServletRequest request, @Restricted(Authority.ROLE_PUBLIC) Admin user) {
        return new AdminIndexView(injector, request);
    }

}
