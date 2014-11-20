package javabot.web.views;

import com.google.inject.Injector;
import javabot.model.Factoid;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public abstract class PagedView extends MainView {
    public static final int ITEMS_PER_PAGE = 50;
    private final Integer nextPage;
    private final Integer previousPage;
    private final Integer pageOffset = ITEMS_PER_PAGE;
    private final Integer page;
    private Integer index;
    private Long itemCount;

    public PagedView(final Injector injector, final HttpServletRequest request, final Integer page) {
        super(injector, request);
        this.page = page;
        this.index = (page - 1) * ITEMS_PER_PAGE;
        nextPage = page + 1;
        previousPage = page == 1 ? null : page - 1;
    }

    public final Long getItemCount() {
        if (itemCount == null) {
            itemCount = countItems();
        }
        return itemCount;
    }

    public abstract Long countItems();

    public Integer getIndex() {
        return index;
    }

    public Long getPageCount() {
        return getItemCount() / 50;
    }

    public Integer getPageOffset() {
        return pageOffset;
    }

    public Integer getNextPage() {
        return nextPage < getPageCount() ? nextPage : null;
    }

    public Integer getPreviousPage() {
        return previousPage;
    }

    public Long getEndRange() {
        return Math.min(getItemCount(), getStartRange() + ITEMS_PER_PAGE - 1);
    }

    public Long getStartRange() {
        return index + 1L;
    }

    public abstract List<Factoid> getPageItems();
}
