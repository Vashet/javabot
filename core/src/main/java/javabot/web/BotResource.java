package javabot.web;

import com.google.inject.Injector;
import io.dropwizard.views.View;
import javabot.web.views.FactoidsView;
import javabot.web.views.IndexView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
    public View factoids(@Context HttpServletRequest request, @QueryParam("page") Integer page) {
        System.out.println("page = " + page);
        return new FactoidsView(BotResource.this.injector, request, page == null ? 1 : page);
    }

}
