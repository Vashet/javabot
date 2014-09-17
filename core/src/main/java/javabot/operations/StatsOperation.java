package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.dao.FactoidDao;
import org.pircbotx.hooks.events.MessageEvent;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

import static java.time.Instant.now;

@SPI(BotOperation.class)
public class StatsOperation extends BotOperation {
    @Inject
    private FactoidDao factoidDao;
    private static final Instant startTime = now();
    private int numberOfMessages = 0;

    @Override
    public final boolean handleMessage(final MessageEvent event) {
        numberOfMessages++;
        final String message = event.getMessage();
        if ("stats".equalsIgnoreCase(message)) {
            getBot().postMessage(event.getChannel(), event.getUser(),
                                 Sofia.botStats(Duration.between(now(), startTime).toDays(), numberOfMessages, factoidDao.count()));
            return true;
        }
        return false;
    }
}