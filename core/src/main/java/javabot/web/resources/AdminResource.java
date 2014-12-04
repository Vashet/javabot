package javabot.web.resources;

import com.google.inject.Injector;
import io.dropwizard.views.View;
import javabot.dao.AdminDao;
import javabot.dao.ConfigDao;
import javabot.model.Admin;
import javabot.model.Config;
import javabot.web.auth.Restricted;
import javabot.web.model.Authority;
import javabot.web.model.User;
import javabot.web.views.AdminIndexView;
import javabot.web.views.ConfigurationView;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Morphia;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/admin")
public class AdminResource {
    @Inject
    private Injector injector;

    @Inject
    private AdminDao adminDao;

    @Inject
    private Morphia morphia;

    @Inject
    private ConfigDao configDao;

    @GET
    public View index(@Context HttpServletRequest request, @Restricted(Authority.ROLE_ADMIN) User user) {
        return new AdminIndexView(injector, request);
    }

    @GET
    @Path("/config")
    public View config(@Context HttpServletRequest request, @Restricted(Authority.ROLE_ADMIN) User user) {
        return new ConfigurationView(injector, request);
    }

    @POST
    @Path("/saveConfig")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public View saveConfig(@Context HttpServletRequest request, @Restricted(Authority.ROLE_ADMIN) User user,
                           @FormParam("server") String server, @FormParam("url") String url, @FormParam("port") Integer port,
                           @FormParam("historyLength") Integer historyLength, @FormParam("trigger") String trigger,
                           @FormParam("nick") String nick, @FormParam("password") String password,
                           @FormParam("throttleThreshold") Integer throttleThreshold,
                           @FormParam("minimumNickServAge") Integer minimumNickServAge) {
        System.out.println("request = [" + request + "], user = [" + user + "], server = [" + server + "], url = [" + url + "], port = ["
                           + port + "], historyLength = [" + historyLength + "], trigger = [" + trigger + "], nick = [" + nick
                           + "], password = [" + password + "], throttleThreshold = [" + throttleThreshold + "], mininumNickServAge = ["
                           + minimumNickServAge + "]");
        Config config = configDao.get();
        config.setServer(server);
        config.setUrl(url);
        config.setPort(port);
        config.setHistoryLength(historyLength);
        config.setTrigger(trigger);
        config.setNick(nick);
        config.setPassword(password);
        config.setThrottleThreshold(throttleThreshold);
        config.setMinimumNickServAge(minimumNickServAge);
        configDao.save(config);
        return new ConfigurationView(injector, request);
    }

    @GET
    @Path("/enableOperation/{name}")
    public View enableOperation(@Context HttpServletRequest request, @Restricted(Authority.ROLE_ADMIN) User user,
                                @PathParam("name") String name) {
        adminDao.enableOperation(name, adminDao.getAdminByEmailAddress(user.getEmail()));
        return new ConfigurationView(injector, request);
    }

    @GET
    @Path("/disableOperation/{name}")
    public View disableOperation(@Context HttpServletRequest request, @Restricted(Authority.ROLE_ADMIN) User user,
                                 @PathParam("name") String name) {
        adminDao.disableOperation(name, adminDao.getAdminByEmailAddress(user.getEmail()));
        return new ConfigurationView(injector, request);
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
        if (admin == null) {
            admin = new Admin();
            admin.setIrcName(ircName);
            admin.setHostName(hostName);
            admin.setEmailAddress(emailAddress);
            adminDao.save(admin);
        }
        return index(request, user);
    }

}
