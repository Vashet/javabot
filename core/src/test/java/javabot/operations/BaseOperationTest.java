package javabot.operations;

import com.antwerkz.sofia.Sofia;
import javabot.BaseTest;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;

public abstract class BaseOperationTest extends BaseTest {
    protected void scanForResponse(final String message, final String target) {
        final List<MessageEvent> list = sendMessage(message);
        boolean found = false;
        for (final MessageEvent response : list) {
            found |= response.getMessage().contains(target);
        }
        Assert.assertTrue(found, String.format("Did not find \n'%s' in \n'%s'", target, list));
    }

    protected void testMessage(final String message, final String... responses) {
        compareResults(sendMessage(message), responses);
    }

    protected void testMessageAs(final User user, final String message, final String... responses) {
        compareResults(sendMessage(user, message), responses);
    }

    private void compareResults(final List<MessageEvent> list, final String[] responses) {
        Assert.assertEquals(list.size(), responses.length, String.format("Should get expected response count back. "
                                                                         + "\n** expected: \n%s"
                                                                         + "\n** got: \n%s", Arrays.toString(responses), list));
        for (final String response : responses) {
            Assert.assertEquals(list.remove(0).getMessage(), response);
        }
        Assert.assertTrue(list.isEmpty(), "All responses should be matched.");
    }

    protected List<MessageEvent> sendMessage(final String message) {
        return sendMessage(getTestUser(), message);
    }

    protected List<MessageEvent> sendMessage(final User testUser, final String message) {
        getJavabot().processMessage(new MessageEvent<>(getIrcBot(), getJavabotChannel(), testUser, message));
        return getJavabot().getMessages();
    }

    protected void testMessageList(final String message, final List<String> responses) {
        final List<MessageEvent> list = sendMessage(message);
        boolean found = false;
        for (final MessageEvent response : list) {
            found |= responses.contains(response.getMessage());
        }
        Assert.assertTrue(found, String.format("Should get one response from the list of possibilities"
                                               + "\n** expected: \n%s"
                                               + "\n** got: \n%s", responses, list));
    }

    protected String getFoundMessage(final String factoid, final String value) {
        return String.format("%s, %s is %s", getTestUser(), factoid, value);
    }

    protected void forgetFactoid(final String name) {
        testMessage(String.format("~forget %s", name), Sofia.factoidForgotten(name, getTestUser().getNick()));
    }
}