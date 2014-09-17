package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.operations.locator.JCPJSRLocator;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

import javax.inject.Inject;

@SPI(BotOperation.class)
public class JSROperation extends BotOperation {
    @Inject
    JCPJSRLocator locator;

    @Override
    public final boolean handleMessage(final MessageEvent event) {
        final String message = event.getMessage().toLowerCase();
        final Channel channel = event.getChannel();
        if ("jsr".equals(message)) {
            getBot().postMessage(channel, event.getUser(), Sofia.jsrMissing());
            return true;
        } else {
            if (message.startsWith("jsr ")) {
                final String jsrString = message.substring("jsr ".length());

                try {
                    final int jsr = Integer.parseInt(jsrString);
                    String response = locator.findInformation(jsr);
                    if (response != null && !response.isEmpty()) {
                        getBot().postMessage(channel, event.getUser(), response);
                    } else {
                        getBot().postMessage(channel, event.getUser(), Sofia.jsrUnknown(jsrString));
                    }
                } catch (NumberFormatException nfe) {
                    getBot().postMessage(channel, event.getUser(), Sofia.jsrInvalid(jsrString));
                }
                return true;
            }
        }
        return false;
    }
}

