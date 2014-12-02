package javabot.web.resources;

import com.google.inject.Injector;
import io.dropwizard.views.View;
import javabot.dao.AdminDao;
import javabot.model.Admin;
import javabot.web.auth.Restricted;
import javabot.web.model.Authority;
import javabot.web.model.User;
import javabot.web.views.AdminIndexView;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

@Path("/admin")
public class AdminResource {
    @Inject
    private Injector injector;

    @Inject
    private AdminDao adminDao;

    @GET
    public View index(@Context HttpServletRequest request, @Restricted(Authority.ROLE_ADMIN) User user) {
        return new AdminIndexView(injector, request);
    }

    @GET
    @Path("/delete/{id}")
    public View deleteAdmin(@Context HttpServletRequest request, @Restricted(Authority.ROLE_ADMIN) User user,
                            @PathParam("id") String id) {
        Admin admin = adminDao.find(new ObjectId(id));
        if (admin != null && !admin.getBotOwner()) {
            adminDao.delete(admin);
        }
        return index(request, user);
    }

    @POST
    @Path("/add")
    public View addAdmin(@Context HttpServletRequest request, @Restricted(Authority.ROLE_ADMIN) User user,
                         @FormParam("ircName") String ircName, @FormParam("hostName") String hostName,
                         @FormParam("emailAddress") String emailAddress) {
        Admin admin = adminDao.getAdminByEmailAddress(emailAddress);
        if(admin == null) {
            admin = new Admin();
            admin.setIrcName(ircName);
            admin.setHostName(hostName);
            admin.setEmailAddress(emailAddress);
            adminDao.save(admin);
        }
        return index(request, user);
    }

}
