package javabot.operations;

import com.antwerkz.maven.SPI;
import org.pircbotx.hooks.events.MessageEvent;

@SPI(BotOperation.class)
public class SayOperation extends BotOperation {
    @Override
    public final boolean handleMessage(final MessageEvent event) {
        final String message = event.getMessage();
        if (message.startsWith("say ")) {
            getBot().postMessage(event.getChannel(), event.getUser(), message.substring("say ".length()));
            return true;
        }
        return false;
    }
}