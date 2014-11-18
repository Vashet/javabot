package javabot.web;

import com.google.inject.Injector;
import io.dropwizard.views.View;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
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
    public View index(@Context HttpServletRequest request) {
        return new IndexView(BotResource.this.injector, request);
    }

    @GET
    @Path("/index")
    @Produces("text/html;charset=ISO-8859-1")
    public View indexHtml(@Context HttpServletRequest request) {
        return index(request);
    }

    @GET
    @Path("/factoids")
    @Produces("text/html;charset=ISO-8859-1")
    public View factoids(@Context HttpServletRequest request) {
        return new MainView(injector, request) {
            @Override
            public String getChildView() throws IOException, WebApplicationException {
                return "/factoids.ftl";
            }
        };
    }

    public static class IndexView extends MainView {
        public IndexView(final Injector injector, final HttpServletRequest request) {
            super(injector, request);
        }

        @Override
        public String getChildView() throws IOException, WebApplicationException {
            return "/index.ftl";
        }
    }
}
