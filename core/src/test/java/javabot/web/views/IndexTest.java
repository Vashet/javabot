package javabot.web.views;

import io.dropwizard.views.freemarker.FreemarkerViewRenderer;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class IndexTest extends ViewsTest{
    @Test
    public void index() throws IOException {
        find(false);
        find(true);
    }

    protected void find(final boolean loggedIn) throws IOException {
        FreemarkerViewRenderer renderer = new FreemarkerViewRenderer();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        renderer.render(new IndexView(getInjector(), new MockServletRequest(loggedIn)), Locale.getDefault(), output);
        Source source = new Source(new ByteArrayInputStream(output.toByteArray()));
        List<Element> a = source.getAllElements("href", "/botadmin/newChannel", false);
        Assert.assertEquals(loggedIn ? 1 : 0, a.size());
    }
}