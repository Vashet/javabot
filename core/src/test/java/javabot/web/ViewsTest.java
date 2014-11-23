package javabot.web;

import com.google.inject.Injector;
import io.dropwizard.views.freemarker.FreemarkerViewRenderer;
import javabot.BaseTest;
import javabot.dao.FactoidDao;
import javabot.model.Factoid;
import javabot.web.views.FactoidsView;
import javabot.web.views.IndexView;
import javabot.web.views.PagedView;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.junit.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ViewsTest extends BaseTest {

    @Inject
    private Injector injector;

    @Inject
    private FactoidDao factoidDao;

    @Test
    public void index() throws IOException {
        find(false);
        find(true);
    }

    protected void find(final boolean loggedIn) throws IOException {
        FreemarkerViewRenderer renderer = new FreemarkerViewRenderer();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        renderer.render(new IndexView(injector, new MockServletRequest(loggedIn)), Locale.getDefault(), output);
        Source source = new Source(new ByteArrayInputStream(output.toByteArray()));
        List<Element> a = source.getAllElements("href", "/botadmin/newChannel", false);
        Assert.assertEquals(loggedIn ? 1 : 0, a.size());
    }

    @Test
    public void singleFactoid() throws IOException {
        createFactoids(1);
        Source source = render(0, new Factoid());
        Element previousPage = source.getElementById("previousPage");
        Element nextPage = source.getElementById("nextPage");
        Element currentPage = source.getElementById("currentPage");
        Assert.assertTrue(previousPage.getStartTag().getAttributeValue("class"),
                          previousPage.getStartTag().getAttributeValue("class").contains("disabled"));
        Assert.assertTrue(nextPage.getStartTag().getAttributeValue("class"),
                          nextPage.getStartTag().getAttributeValue("class").contains("disabled"));
        String content = currentPage.getContent().toString().trim();
        Assert.assertEquals("Displaying 1 to 1 of 1", content);
    }

    @Test
    public void factoidFilter() throws IOException {
        createFactoids(10);
        Source source = render(0, new Factoid("name 1", null, null));
        Element previousPage = source.getElementById("previousPage");
        Element nextPage = source.getElementById("nextPage");
        Element currentPage = source.getElementById("currentPage");
        Assert.assertTrue(previousPage.getStartTag().getAttributeValue("class"),
                          previousPage.getStartTag().getAttributeValue("class").contains("disabled"));
        Assert.assertTrue(nextPage.getStartTag().getAttributeValue("class"),
                          nextPage.getStartTag().getAttributeValue("class").contains("disabled"));
        String content = currentPage.getContent().toString().trim();
        Assert.assertEquals("Displaying 1 to 1 of 1", content);
    }
    @Test
    public void factoidBadFilter() throws IOException {
        createFactoids(10);
        Source source = render(0, new Factoid("bad filter", null, null));
        Element previousPage = source.getElementById("previousPage");
        Element nextPage = source.getElementById("nextPage");
        Element currentPage = source.getElementById("currentPage");
        Assert.assertTrue(previousPage.getStartTag().getAttributeValue("class"),
                          previousPage.getStartTag().getAttributeValue("class").contains("disabled"));
        Assert.assertTrue(nextPage.getStartTag().getAttributeValue("class"),
                          nextPage.getStartTag().getAttributeValue("class").contains("disabled"));
        String content = currentPage.getContent().toString().trim();
        Assert.assertEquals("Displaying 0 to 0 of 0", content);
    }

    @Test
    public void twoFactoidPages() throws IOException {
        int count = (int) (PagedView.ITEMS_PER_PAGE * 1.5);
        createFactoids(count);

        Source source = render(0, new Factoid());
        Element previousPage = source.getElementById("previousPage");
        Element nextPage = source.getElementById("nextPage");
        Element currentPage = source.getElementById("currentPage");
        Assert.assertTrue(previousPage.getStartTag().getAttributeValue("class").contains("disabled"));
        Assert.assertFalse(nextPage.getStartTag().getAttributeValue("class").contains("disabled"));
        String content = currentPage.getContent().toString().trim();
        Assert.assertEquals(String.format("Displaying 1 to %s of %s", PagedView.ITEMS_PER_PAGE, count), content);

        source = render(2, new Factoid());
        previousPage = source.getElementById("previousPage");
        nextPage = source.getElementById("nextPage");
        currentPage = source.getElementById("currentPage");
        Assert.assertFalse(previousPage.getStartTag().getAttributeValue("class").contains("disabled"));
        Assert.assertTrue(nextPage.getStartTag().getAttributeValue("class").contains("disabled"));
        content = currentPage.getContent().toString().trim();
        Assert.assertEquals(String.format("Displaying %s to %s of %s",
                                          PagedView.ITEMS_PER_PAGE + 1, count, count), content);

        source = render(3, new Factoid());
        previousPage = source.getElementById("previousPage");
        nextPage = source.getElementById("nextPage");
        currentPage = source.getElementById("currentPage");
        Assert.assertFalse(previousPage.getStartTag().getAttributeValue("class"),
                           previousPage.getStartTag().getAttributeValue("class").contains("disabled"));
        Assert.assertTrue(nextPage.getStartTag().getAttributeValue("class"),
                          nextPage.getStartTag().getAttributeValue("class").contains("disabled"));
        content = currentPage.getContent().toString().trim();
        Assert.assertEquals(String.format("Displaying %s to %s of %s",
                                          PagedView.ITEMS_PER_PAGE + 1, count, count), content);
    }

    protected Source render(final int page, final Factoid filter) throws IOException {
        FreemarkerViewRenderer renderer = new FreemarkerViewRenderer();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        renderer.render(new FactoidsView(injector, new MockServletRequest(false), page, filter), Locale.getDefault(), output);
        return new Source(new ByteArrayInputStream(output.toByteArray()));
    }

    private void createFactoids(final int count) {
        factoidDao.deleteAll();
        for (int i = 0; i < count; i++) {
            factoidDao.save(new Factoid("name " + i, "value " + i, "userName " + i));
        }
    }

}