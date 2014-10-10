package javabot.commands;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.dao.ApiDao;
import javabot.javadoc.JavadocApi;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.MessageEvent;

import javax.inject.Inject;

@SPI({AdminCommand.class})
public class DropApi extends AdminCommand {
    @Inject
    private ApiDao dao;
    @Param
    String name;

    @Override
    public void execute(final MessageEvent event) {
        final Channel channel = event.getChannel();
        final JavadocApi api = dao.find(name);
        if (api != null) {
            drop(event, channel, api);
        } else {
            getBot().postMessage(channel, event.getUser(), Sofia.unknownApi(name, event.getUser().getNick()));
        }
    }

    private void drop(final MessageEvent event, final Channel channel, final JavadocApi api) {
        getBot().postMessage(channel, event.getUser(), Sofia.adminRemovingOldJavadoc(api.getName()));
        dao.delete(api);
        getBot().postMessage(channel, event.getUser(), Sofia.adminDoneRemovingOldJavadoc(api.getName()));
    }
}