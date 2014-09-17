package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.Javabot;
import org.pircbotx.hooks.events.MessageEvent;

@SPI(BotOperation.class)
public class IgnoreOperation extends BotOperation {
    @Override
    public final boolean handleMessage(final MessageEvent event) {
        final String message = event.getMessage();
        if (message.startsWith("ignore ")) {
            final String[] parts = message.split(" ");
            getBot().addIgnore(parts[1]);
            getBot().postMessage(event.getChannel(), event.getUser(), Sofia.botIgnoring(parts[1]));
            return true;
        }
        return false;
    }
}
