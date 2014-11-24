package javabot.web.views;

import com.google.inject.Injector;
import io.dropwizard.views.freemarker.FreemarkerViewRenderer;
import javabot.BaseTest;
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
    protected Injector injector;

    protected void checkRange(final Source source, final int fromt, final int to, final int of) {
        String content = source.getElementById("currentPage").getContent().toString().trim();
        Assert.assertEquals(String.format("Displaying %d to %d of %d", fromt, to, of), content);
    }

    protected void nextEnabled(final Source source) {
        Assert.assertFalse(source.getElementById("nextPage").getStartTag().getAttributeValue("class"),
                           source.getElementById("nextPage").getStartTag().getAttributeValue("class").contains("disabled"));
    }

    protected void nextDisabled(final Source source) {
        Assert.assertTrue(source.getElementById("nextPage").getStartTag().getAttributeValue("class"),
                          source.getElementById("nextPage").getStartTag().getAttributeValue("class").contains("disabled"));
    }

    protected void previousDisabled(final Source source) {
        Assert.assertTrue("Previous page should be disabled",
                          source.getElementById("previousPage").getStartTag().getAttributeValue("class").contains("disabled"));
    }

    protected void previousEnabled(final Source source) {
        Assert.assertFalse("Previous page should be disabled",
                           source.getElementById("previousPage").getStartTag().getAttributeValue("class").contains("disabled"));
    }
}