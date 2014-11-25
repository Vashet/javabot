package javabot.web.views;

import com.google.inject.Injector;
import javabot.dao.AdminDao;
import javabot.model.Admin;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.List;

public class AdminIndexView extends MainView {

    @Inject
    private AdminDao adminDao;

    public AdminIndexView(final Injector injector, final HttpServletRequest request) {
        super(injector, request);
    }

    public List<Admin> getAdmins() {
        List<Admin> all = adminDao.findAll();
        return all;
    }

    @Override
    public String getChildView() {
        return "admin/index.ftl";
    }
}
