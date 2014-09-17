package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@SPI(BotOperation.class)
public class DaysUntilOperation extends BotOperation {
    @Override
    public final boolean handleMessage(final MessageEvent event) {
        String message = event.getMessage().toLowerCase();
        boolean handled = false;
        if (message.startsWith("days until ")) {
            final User sender = event.getUser();
            message = message.substring("days until ".length());
            LocalDateTime d = null;
            final DateTimeFormatter[] formats = {
                                                    DateTimeFormatter.ofPattern("yyyy/MM/dd"), DateTimeFormatter.ofPattern("MMM d, ''yy"),
                                                    DateTimeFormatter.ofPattern("d MMM yyyy"), DateTimeFormatter.ofPattern("MMM d, yyyy"),
                                                    DateTimeFormatter.ofPattern("MMM d, ''yy")
            };
            int i = 0;
            while (i < formats.length && d == null) {
                try {
                    d = LocalDateTime.parse(message, formats[i]);
                    calcTime(event, d);
                } catch (IllegalArgumentException e) {
                    // I think we just want to ignore this...
                }
                i++;
            }
            if (d == null) {
                getBot().postMessage(event.getChannel(), event.getUser(), Sofia.invalidDateFormat(sender));
                handled = true;
            }
        }
        return handled;
    }

    private void calcTime(final MessageEvent event, final LocalDateTime d) {
        final Long days = Duration.between(d, LocalDateTime.now().withHour(0)).toDays();
        long l = d.toLocalDate().toEpochDay();
        getBot().postMessage(event.getChannel(), event.getUser(), Sofia.daysUntil(event.getUser().getNick(), days, new Date(l)));
    }
}
