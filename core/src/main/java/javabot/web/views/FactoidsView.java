package javabot.web.views;

import com.google.common.base.Strings;
import com.google.inject.Injector;
import javabot.dao.FactoidDao;
import javabot.dao.util.QueryParam;
import javabot.model.Factoid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FactoidsView extends PagedView {
    private static final Logger LOG = LoggerFactory.getLogger(FactoidsView.class);

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd hh:mm");
    private final Factoid filter;
    private Long itemCount = 0L;

    @Inject
    private FactoidDao factoidDao;

    public FactoidsView(final Injector injector, final HttpServletRequest request, final Integer page, final Factoid filter) {
        super(injector, request, page);
        this.filter = filter;
        itemCount = factoidDao.factoidCountFiltered(filter);
    }

    @Override
    protected String getPageUrl() {
        return "/factoids";
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
        return factoidDao.getFactoidsFiltered(new QueryParam(getIndex(), ITEMS_PER_PAGE, "Name", true), filter);
    }

    @Override
    public String getNextPage() {
        return applyFilter(super.getNextPage());
    }

    @Override
    public String getPreviousPage() {
        return applyFilter(super.getPreviousPage());
    }

    private String applyFilter(final String url) {
        try {
            if (url != null) {
                StringBuilder builder = new StringBuilder();
                if (!Strings.isNullOrEmpty(filter.getName())) {
                    builder.append("&name=")
                           .append(encode(filter.getName()));
                }
                if (!Strings.isNullOrEmpty(filter.getValue())) {
                    builder.append("&value=")
                           .append(encode(filter.getValue()));
                }
                if (!Strings.isNullOrEmpty(filter.getUserName())) {
                    builder.append("&userName=")
                           .append(encode(filter.getUserName()));
                }
                return url + builder;
            }
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
        }
        return url;
    }

    public String format(final LocalDateTime date) {
        return DATE_TIME_FORMATTER.format(date);
    }

    @Override
    public String getChildView() throws IOException, WebApplicationException {
        return "/factoids.ftl";
    }

}
