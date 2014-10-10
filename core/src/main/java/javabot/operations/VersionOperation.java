package javabot.operations;

import java.util.ArrayList;
import java.util.List;

import com.antwerkz.maven.SPI;
import javabot.IrcEvent;
import javabot.Message;

@SPI(StandardOperation.class)
public class VersionOperation extends StandardOperation {
    @Override
    public final boolean handleMessage(final MessageEvent event) {
        final String message = event.getMessage();
        final List<Message> responses = new ArrayList<Message>();
        if ("version".equalsIgnoreCase(message)) {
            responses.add(new Message(event.getChannel(), event, String.format("I am currently running version %s",
                getBot().loadVersion())));
        }
        return responses;
    }
}