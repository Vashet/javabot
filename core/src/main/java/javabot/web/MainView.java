package javabot.web;

import com.google.common.base.Charsets;
import io.dropwizard.views.View;
import javabot.dao.FactoidDao;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;

public abstract class MainView extends View {

    @Inject
    private FactoidDao factoidDao;

    public MainView() {
        super("/main.ftl", Charsets.ISO_8859_1);
    }

    public abstract String getChildView() throws IOException, WebApplicationException;

    public long getFactoidCount() {
        return factoidDao.count();
    }
}
