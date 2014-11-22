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
    private final Factoid filter;
    private Long itemCount = 0L;

    @Inject
    private FactoidDao factoidDao;

    public FactoidsView(final Injector injector, final HttpServletRequest request, final Integer page, final Factoid filter) {
        super(injector, request, page);
        this.filter = filter;
        itemCount = factoidDao.count();
    }

    @Override
    public Long countItems() {
        return itemCount;
    }

    public Factoid getFilter() {
        return filter;
    }

    @Override
    public List<Factoid> getPageItems() {
        QueryParam qp = new QueryParam(getIndex(),  ITEMS_PER_PAGE, "Name", true);
        return factoidDao.getFactoidsFiltered(qp, filter);
    }

    public String format(final LocalDateTime date) {
        return DATE_TIME_FORMATTER.format(date);
    }

    @Override
    public String getChildView() throws IOException, WebApplicationException {
        return "/factoids.ftl";
    }

}
