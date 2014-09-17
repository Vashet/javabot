package javabot.commands;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.dao.ConfigDao;
import javabot.model.Config;
import org.pircbotx.hooks.events.MessageEvent;

import javax.inject.Inject;

@SPI({AdminCommand.class})
public class EnableOperation extends OperationsCommand {
    @Param
    String name;

    @Inject
    private ConfigDao configDao;

    @Override
    public void execute(final MessageEvent event) {
        if (getBot().enableOperation(name)) {
            Config config = configDao.get();
            config.getOperations().add(name);
            configDao.save(config);
            getBot().postMessage(event.getChannel(), event.getUser(), Sofia.adminOperationEnabled(name));
            listCurrent(event);
        } else {
            getBot().postMessage(event.getChannel(), event.getUser(), Sofia.adminOperationNotEnabled(name));
        }
    }
}
