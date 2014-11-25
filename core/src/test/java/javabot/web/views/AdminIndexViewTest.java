package javabot.web.views;

import javabot.dao.AdminDao;
import net.htmlparser.jericho.Source;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.io.IOException;

public class AdminIndexViewTest extends AdminViewTest {
    @Inject
    private AdminDao adminDao;

    @Test
    public void addAdmin() throws IOException {
        Source source = render();

        System.out.println("adminDao = " + adminDao.findAll());
        long count = adminDao.count();
        for (int i = 0; i < count; i++) {
            Assert.assertNotNull(source.getElementById("admin" + count), "Trying to find admin" + count);
        }
    }
}
