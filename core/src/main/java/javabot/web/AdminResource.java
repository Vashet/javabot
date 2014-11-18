package javabot.web;

import com.google.inject.Injector;
import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import java.io.IOException;

@Path("/botadmin")
public class AdminResource {
    @Inject
    private Injector injector;

    @GET
    public View index(@Context HttpServletRequest request, @Auth String credentials) {
        return new AdminView(injector, request);
    }

    private class AdminView extends MainView {

        public AdminView(final Injector injector, final HttpServletRequest request) {
            super(injector, request);
        }

        @Override
        public String getChildView() throws IOException, WebApplicationException {
            return "/admin.ftl";
        }
    }
}
