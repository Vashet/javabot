package javabot.commands;

import com.antwerkz.sofia.Sofia;
import javabot.operations.BotOperation;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.stream.Collectors;

public abstract class OperationsCommand extends AdminCommand {
    protected void listCurrent(final MessageEvent event) {
        getBot().postMessage(event.getChannel(), event.getUser(), Sofia.adminRunningOperations(event.getUser().getNick()));
        getBot().postMessage(event.getChannel(), event.getUser(),
                             String.join(",", getBot().getAllOperations()
                                                      .stream()
                                                      .map(BotOperation::getName)
                                                      .collect(Collectors.toList())));
    }
}