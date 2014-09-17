package javabot.operations;

import com.antwerkz.maven.SPI;
import javabot.dao.ConfigDao;
import org.pircbotx.hooks.events.MessageEvent;

import javax.inject.Inject;

@SPI(BotOperation.class)
public class QuitOperation extends BotOperation {

    @Inject
    private ConfigDao configDao;

    @Override
    public final boolean handleMessage(final MessageEvent event) {
        final String message = event.getMessage();
        if (message.toLowerCase().startsWith("quit ")) {
            if (message.substring("quit ".length()).equals(configDao.get().getPassword())) {
                System.exit(0);
            }
        }
        return true;
    }
}