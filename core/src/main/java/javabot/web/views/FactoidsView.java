package javabot.web.views;

import com.google.inject.Injector;
import javabot.dao.FactoidDao;
import javabot.dao.util.QueryParam;
import javabot.model.Factoid;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FactoidsView extends PagedView {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm");
    private Long itemCount = 0L;

    @Inject
    private FactoidDao factoidDao;

    public FactoidsView(final Injector injector, final HttpServletRequest request, final Integer page) {
        super(injector, request, page);
        itemCount = factoidDao.count();
    }

    @Override
    public Long countItems() {
        return itemCount;
    }

    @Override
    public List<Factoid> getPageItems() {
        QueryParam qp = new QueryParam(getIndex(),  ITEMS_PER_PAGE, "Name", true);
        return factoidDao.getFactoidsFiltered(qp, getFactoidFilter());
    }

    private Factoid getFactoidFilter() {
        Factoid filter = (Factoid) getRequest().getSession().getAttribute("factoidFilter");
        return filter == null ? new Factoid(): filter;
    }

    public String format(final LocalDateTime date) {
        return DATE_TIME_FORMATTER.format(date);
    }

    @Override
    public String getChildView() throws IOException, WebApplicationException {
        return "/factoids.ftl";
    }

}
