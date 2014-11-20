package javabot.web;

import com.google.inject.Injector;
import io.dropwizard.views.freemarker.FreemarkerViewRenderer;
import javabot.BaseTest;
import javabot.web.views.FactoidsView;
import javabot.web.views.IndexView;
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
    public void factoids() throws IOException {
        FreemarkerViewRenderer renderer = new FreemarkerViewRenderer();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        renderer.render(new FactoidsView(injector, new MockServletRequest(false), 0), Locale.getDefault(), output);
        Source source = new Source(new ByteArrayInputStream(output.toByteArray()));
        System.out.println("source = " + source);
    }

}