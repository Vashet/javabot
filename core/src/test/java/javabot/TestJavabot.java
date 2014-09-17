package javabot;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;

public class TestJavabot extends Javabot {
    private List<MessageEvent> messages = new ArrayList<>();

    public List<MessageEvent> getMessages() {
        final List<MessageEvent> list = messages;
        messages = new ArrayList<>();
        return list;
    }

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
        messages.add(new MessageEvent<>(getIrcBot(), channel, user, message));
    }

    @Override
    public boolean isOnCommonChannel(final User user) {
        return true;
    }
}
