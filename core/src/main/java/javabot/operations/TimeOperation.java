package javabot.operations;

import com.antwerkz.maven.SPI;
import javabot.Message;

import java.util.Calendar;

@SPI(BotOperation.class)
public class TimeOperation extends BotOperation {
    @Override
    public boolean handleMessage(final Message event) {
        final String message = event.getMessage();
        if ("time".equals(message) || "date".equals(message)) {
            getBot().postMessage(event.getChannel(), event.getUser(), Calendar.getInstance().getTime().toString());
            return true;
        }
        return false;
    }
}
