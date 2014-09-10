package javabot.operations;

import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created Dec 16, 2008
 *
 * @author <a href="mailto:jlee@antwerkz.com">Justin Lee</a>
 */
public abstract class UrlOperation extends BotOperation {
    private static final Logger log = LoggerFactory.getLogger(UrlOperation.class);

    @Override
    public final boolean handleMessage(final MessageEvent event) {
        String message = event.getMessage();
        if (message.startsWith(getTrigger())) {
            message = message.substring(getTrigger().length());
            try {
                getBot().postMessage(event.getChannel(), event.getUser(),
                                     getBaseUrl() + URLEncoder.encode(message, Charset.defaultCharset().displayName()));
                return true;
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        }
        return false;
    }

    protected abstract String getBaseUrl();

    protected abstract String getTrigger();
}