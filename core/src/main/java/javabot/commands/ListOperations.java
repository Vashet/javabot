package javabot.commands;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.operations.BotOperation;
import org.apache.commons.lang.StringUtils;
import org.pircbotx.hooks.events.MessageEvent;

@SPI({AdminCommand.class})
public class ListOperations extends OperationsCommand {
    @Override
    public void execute(final MessageEvent event) {
        getBot().postMessage(event.getChannel(), event.getUser(),
                             Sofia.adminKnownOperations(event.getUser().getNick(), StringUtils.join(BotOperation.list().iterator(), ",")));

        listCurrent(event);
        getBot().postMessage(event.getChannel(), event.getUser(), Sofia.adminOperationInstructions());
    }
}