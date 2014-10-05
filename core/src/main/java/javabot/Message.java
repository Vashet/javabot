package javabot;

import org.pircbotx.Channel;
import org.pircbotx.User;

public class Message {
    private final Channel channel;
    private final User user;
    private String message;

    public Message(final Channel dest, final User user, final String message) {
        channel = dest;
        this.user = user;
        this.message = message;
    }

    public Message(final User dest, final String message) {
        channel = null;
        user = dest;
        this.message = message;
    }

    public Message(final Message message, final String value) {
        channel = message.getChannel();
        user = message.getUser();
        this.message = value;
    }

    public Channel getChannel() {
        return channel;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
