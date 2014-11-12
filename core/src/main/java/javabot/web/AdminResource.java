package javabot.web;

import com.codahale.metrics.annotation.Gauge;
import com.google.inject.Injector;
import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;

@Path("/botadmin")
public class AdminResource {
    @Inject
    private Injector injector;

    @GET
    public View index(@Auth String credentials) {
        return injector.getInstance(AdminView.class);
    }

    private class AdminView extends MainView {
        @Override
        public String getChildView() throws IOException, WebApplicationException {
            return "/admin.ftl";
        }
    }
}
