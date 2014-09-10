package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.dao.FactoidDao;
import javabot.model.Factoid;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@SPI(StandardOperation.class)
public class GetFactoidOperation extends StandardOperation {
    @Inject
    private FactoidDao factoidDao;

    @Override
    public boolean handleMessage(final MessageEvent event) {
        return tell(event) || getFactoid(null, event, new HashSet<>());
    }

    private boolean getFactoid(final TellSubject subject, final MessageEvent event, final Set<String> backtrack) {
        String message = event.getMessage();
        if (message.endsWith(".") || message.endsWith("?") || message.endsWith("!")) {
            message = message.substring(0, message.length() - 1);
        }
        final String firstWord = message.split(" ")[0];
        final String params = message.substring(firstWord.length()).trim();
        Factoid factoid = factoidDao.getFactoid(message.toLowerCase());
        if (factoid == null) {
            factoid = factoidDao.getParameterizedFactoid(firstWord);
        }

        return factoid != null && getResponse(subject, event, backtrack, params, factoid);
    }

    private boolean getResponse(final TellSubject subject, final MessageEvent event, final Set<String> backtrack,
                                final String replacedValue, final Factoid factoid) {
        String sender = event.getUser().getNick();
        final String message = factoid.evaluate(subject, sender, replacedValue);
        if (message.startsWith("<see>")) {
            if (backtrack.contains(message)) {
                getBot().postMessage(event.getChannel(), null, Sofia.factoidLoop(message));
                return true;
            } else {
                backtrack.add(message);
                return getFactoid(subject, event, backtrack);
            }
        } else if (message.startsWith("<reply>")) {
            getBot().postMessage(event.getChannel(), null, message.substring("<reply>".length()));
            return true;
        } else if (message.startsWith("<action>")) {
            getBot().postAction(event.getChannel(), message.substring("<action>".length()));
            return true;
        } else {
            getBot().postMessage(event.getChannel(), null, message);
            return true;
        }
    }

    private boolean tell(final MessageEvent event) {
        final String message = event.getMessage();
        final Channel channel = event.getChannel();
        final User sender = event.getUser();
        boolean handled = false;
        if (isTellCommand(message)) {
            final TellSubject tellSubject = parseTellSubject(event);
            if (tellSubject == null) {
                getBot().postMessage(event.getChannel(), event.getUser(), Sofia.factoidTellSyntax(sender));
                handled = true;
            } else {
                User user = tellSubject.getTarget();
                if (user != null) {
                    if ("me".equalsIgnoreCase(user.getNick())) {
                        user = sender;
                    }
                    final String thing = tellSubject.getSubject();
                    if (user.getNick().equalsIgnoreCase(event.getBot().getNick())) {
                        getBot().postMessage(event.getChannel(), event.getUser(), Sofia.botSelfTalk());
                        handled = true;
                    } else {
                        if (!getBot().isOnCommonChannel(user)) {
                            getBot().postMessage(event.getChannel(), event.getUser(), Sofia.userNotInChannel(user, channel));
                            handled = true;
                        } else if (sender.getNick().equals(channel.getName()) && !getBot().isOnCommonChannel(user)) {
                            getBot().postMessage(event.getChannel(), event.getUser(), Sofia.userNoSharedChannels());
                            handled = true;
                        } else if (thing.endsWith("++") || thing.endsWith("--")) {
                            getBot().postMessage(event.getChannel(), event.getUser(), Sofia.notAllowed());
                            handled = true;
                        } else {
                            handled = getBot().getResponses(new MessageEvent<>(event.getBot(), channel, user, thing),
                                                            event.getUser());
                        }
                    }
                }
            }
        }
        return handled;
    }

    private TellSubject parseTellSubject(final MessageEvent event) {
        String message = event.getMessage();
        if (message.startsWith("tell ")) {
            return parseLonghand(event);
        }
        return parseShorthand(event);
    }

    private TellSubject parseLonghand(final MessageEvent event) {
        String message = event.getMessage();
        final String body = message.substring("tell ".length());
        final String nick = body.substring(0, body.indexOf(" "));
        final int about = body.indexOf("about ");
        if (about < 0) {
            return null;
        }
        final String thing = body.substring(about + "about ".length());
        return new TellSubject(event.getBot().getUserChannelDao().getUser(nick), thing);
    }

    private TellSubject parseShorthand(final MessageEvent event) {
        String target = event.getMessage();
        for (final String start : getBot().getStartStrings()) {
            if (target.startsWith(start)) {
                target = target.substring(start.length()).trim();
            }
        }
        final int space = target.indexOf(' ');
        User user = event.getBot().getUserChannelDao().getUser(target.substring(0, space));
        return space < 0 ? null : new TellSubject(user, target.substring(space + 1).trim());
    }

    private boolean isTellCommand(final String message) {
        return message.startsWith("tell ") || message.startsWith("~");
    }
}