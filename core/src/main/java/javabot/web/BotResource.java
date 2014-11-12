package javabot.web;

import com.google.inject.Injector;
import io.dropwizard.views.View;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BotResource {
    @Inject
    private Injector injector;

    @GET
    @Produces("text/html;charset=ISO-8859-1")
    public View index() {
        return injector.getInstance(IndexView.class);
    }

    private static class IndexView extends MainView {
        @Override
        public String getChildView() throws IOException, WebApplicationException {
            return "/index.ftl";
        }
    }
}
