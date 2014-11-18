package javabot.web;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.inject.Injector;
import io.dropwizard.views.View;
import javabot.dao.AdminDao;
import javabot.dao.ChannelDao;
import javabot.dao.FactoidDao;
import javabot.model.Channel;
import javabot.web.model.InMemoryUserCache;
import javabot.web.model.User;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

public abstract class MainView extends View {

    private final HttpServletRequest request;

    @Inject
    private AdminDao adminDao;

    @Inject
    private ChannelDao channelDao;

    @Inject
    private FactoidDao factoidDao;

    public MainView(final Injector injector, final HttpServletRequest request) {
        super("/main.ftl", Charsets.ISO_8859_1);
        injector.injectMembers(this);
        this.request = request;
    }

    public abstract String getChildView() throws IOException, WebApplicationException;

    public long getFactoidCount() {
        return factoidDao.count();
    }

    public boolean loggedIn() {
        Cookie cookie = getSessionCookie();
        return cookie != null && InMemoryUserCache.INSTANCE.getBySessionToken(UUID.fromString(cookie.getValue())).isPresent();
    }

    public boolean isAdmin() {
        Cookie cookie = getSessionCookie();
        if (cookie != null) {
            Optional<User> optional = InMemoryUserCache.INSTANCE.getBySessionToken(UUID.fromString(cookie.getValue()));
            return optional.isPresent() && adminDao.getAdminByEmailAddress(optional.get().getEmail()) != null;
        } else {
            return false;
        }
    }

    public String getCurrentChannel() {
        return "";
    }

    public List<Channel> getChannels() {
        return channelDao.getChannels(isAdmin());
    }

    public String encode(String value) throws UnsupportedEncodingException {
        return java.net.URLEncoder.encode(value, "UTF-8");
    }

    private Cookie getSessionCookie() {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(JavabotConfiguration.SESSION_TOKEN_NAME)) {
                return cookie;
            }
        }
        return null;
    }
}
