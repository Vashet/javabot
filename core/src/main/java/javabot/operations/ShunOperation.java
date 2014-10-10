package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.dao.ShunDao;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Causes the bot to disregard bot triggers for a few minutes. Useful to de-fang abusive users without ejecting the bot from a channel
 * entirely.
 */
@SPI(BotOperation.class)
public class ShunOperation extends BotOperation {
    private static final Logger LOG = LoggerFactory.getLogger(ShunOperation.class);

    @Inject
    private ShunDao shunDao;

    public final boolean handleMessage(final MessageEvent event) {
        final String message = event.getMessage();
        if (message.startsWith("shun ")) {
            final String[] parts = message.substring(5).split(" ");
            if (parts.length == 0) {
                getBot().postMessage(event.getChannel(), event.getUser(), Sofia.shunUsage());
            } else {
                getBot().postMessage(event.getChannel(), event.getUser(), getShunnedMessage(parts));
            }
            return true;
        }
        return false;
    }

    private String getShunnedMessage(final String[] parts) {
        final String victim = parts[0];
        if (shunDao.isShunned(victim)) {
            return Sofia.alreadyShunned(victim);
        }
        final LocalDateTime until = parts.length == 1
                                    ? LocalDateTime.now().plusMinutes(5)
                                    : LocalDateTime.now().plusSeconds(Integer.parseInt(parts[1]));
        shunDao.addShun(victim, until);

        long l = until.toEpochSecond(ZoneOffset.UTC);

        return String.format(Sofia.shunned(victim, new Date(l)));
    }
}