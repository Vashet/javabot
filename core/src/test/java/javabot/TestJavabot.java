package javabot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.pircbotx.User;

@Singleton
public class TestJavabot extends Javabot {
    @Inject
    private Messages messages;

    @Override
    public void connect() {
    }

    @Override
    public String getNick() {
        return BaseTest.TEST_NICK;
    }

    @Override
    public void postAction(final org.pircbotx.Channel channel, String message) {
        postMessage(channel, null, message);
    }

    @Override
    public void postMessage(final org.pircbotx.Channel channel, final User user, String message) {
        logMessage(channel, user, message);
        log.info(message);
        messages.add(new Message(channel, user, message));
    }

    @Override
    public boolean isOnCommonChannel(final User user) {
        return true;
    }
}
