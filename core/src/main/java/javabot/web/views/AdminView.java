package javabot.web.views;

import com.google.inject.Injector;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;

public class AdminView extends MainView {

    public AdminView(final Injector injector, final HttpServletRequest request) {
        super(injector, request);
    }

    @Override
    public String getChildView() {
        return "/admin.ftl";
    }
}
