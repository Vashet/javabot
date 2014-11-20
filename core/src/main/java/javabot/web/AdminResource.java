package javabot.web;

import com.google.inject.Injector;
import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;
import javabot.web.views.AdminView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/botadmin")
public class AdminResource {
    @Inject
    private Injector injector;

    @GET
    public View index(@Context HttpServletRequest request, @Auth String credentials) {
        return new AdminView(injector, request);
    }

}
