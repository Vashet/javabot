package javabot.operations;

import com.antwerkz.sofia.Sofia;
import javabot.BaseMessagingTest;
import javabot.dao.ChangeDao;
import javabot.dao.FactoidDao;
import org.testng.annotations.Test;

import javax.inject.Inject;

@Test(groups = {"operations"})
public class ForgetFactoidOperationTest extends BaseMessagingTest {
    @Inject
    private FactoidDao factoidDao;
    @Inject
    private ChangeDao changeDao;

    public void forgetFactoid() {
        if (!factoidDao.hasFactoid("afky")) {
            factoidDao.addFactoid(getTestUser().getNick(), "afky", "test");
        }
        testMessage("~forget afky", Sofia.factoidForgotten("afky", getTestUser().getNick()));
    }

    public void nonexistantFactoid() {
        testMessage("~forget asdfghjkl", Sofia.factoidDeleteUnknown("asdfghjkl", getTestUser().getNick()));
    }
}