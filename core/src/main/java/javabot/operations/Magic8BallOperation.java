package javabot.operations;

import java.util.ArrayList;
import java.util.List;

import com.antwerkz.maven.SPI;
import javabot.IrcEvent;
import javabot.Message;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

@SPI(BotOperation.class)
public class Magic8BallOperation extends BotOperation {
    String[] responses = {
        "Yes",
        "Definitely",
        "Absolutely",
        "I think so",
        "I guess that would be good",
        "I'm not really sure",
        "If you want",
        "Well, if you really want to",
        "Maybe",
        "Probably not",
        "Not really",
        "Sometimes",
        "Hmm, sounds bad",
        "No chance!",
        "No way!",
        "No",
        "Absolutely not!",
        "Definitely not!",
        "Don't do anything I wouldn't do",
        "Only on a Tuesday",
        "If I tell you that I'll have to shoot you",
        "I'm getting something about JFK, but I don't think it's relevant"
    };

    @Override
    public final boolean handleMessage(final MessageEvent event) {
        final String message = event.getMessage().toLowerCase();
        final Channel channel = event.getChannel();
        if (message.startsWith("should i ") || message.startsWith("magic8 ")) {
            getBot().postMessage(channel, event.getUser(), responses[((int) (Math.random() * responses.length))]);
            return true;
        }
        return false;
    }
}
