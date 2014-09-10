package javabot.commands;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.dao.AdminDao;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import javax.inject.Inject;

/**
 * Created Dec 17, 2008
 *
 * @author <a href="mailto:jlee@antwerkz.com">Justin Lee</a>
 */
@SPI({AdminCommand.class})
public class AddAdmin extends AdminCommand {
    @Inject
    private AdminDao dao;
    @Param
    String userName;
    @Param
    String hostName;

    @Override
    public void execute(final MessageEvent event) {
        final User user = getJavabot().findUser(userName);
        if (user == null) {
            getJavabot().postMessage(event.getChannel(), event.getUser(), Sofia.userNotFound(userName));
        } else {
            if (dao.getAdmin(user.getNick(), hostName) != null) {
                getJavabot().postMessage(event.getChannel(), event.getUser(), Sofia.adminAlready(user.getNick()));
            } else {
                dao.create(user.getNick(), user.getLogin(), user.getHostmask());
                getJavabot().postMessage(event.getChannel(), event.getUser(), Sofia.adminAdded(user.getNick()));
            }
        }
    }
}
