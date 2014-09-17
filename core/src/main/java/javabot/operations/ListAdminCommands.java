package javabot.operations;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.commands.AdminCommand;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

@SPI({AdminCommand.class})
public class ListAdminCommands extends AdminCommand {
    @Override
    public void execute(MessageEvent event) {
        List<String> list = listCommands()
                                .stream()
                                .map(AdminCommand::getCommandName)
                                .collect(Collectors.toList());
        getBot().postMessage(event.getChannel(), event.getUser(), Sofia.adminKnownCommands(event.getUser().getNick(),
                                                                                           String.join(", ", list)));
    }

    public List<AdminCommand> listCommands() {
        final ServiceLoader<AdminCommand> loader = ServiceLoader.load(AdminCommand.class);
        final List<AdminCommand> list = new ArrayList<>();
        for (final AdminCommand command : loader) {
            list.add(command);
        }
        return list;
    }
}