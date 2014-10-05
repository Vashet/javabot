package javabot.commands;

import com.antwerkz.maven.SPI;
import com.antwerkz.sofia.Sofia;
import javabot.Message;
import javabot.dao.ConfigDao;
import javabot.model.Config;

import javax.inject.Inject;

@SPI({AdminCommand.class})
public class DisableOperation extends OperationsCommand {
    @Param
    String name;

    @Inject
    private ConfigDao configDao;

    @Override
    public void execute(final Message event) {
        if (getBot().disableOperation(name)) {
            Config config = configDao.get();
            config.getOperations().remove(name);
            configDao.save(config);
            getBot().postMessage(event.getChannel(), event.getUser(), Sofia.adminOperationDisabled(name));
            listCurrent(event);
        } else {
            getBot().postMessage(event.getChannel(), event.getUser(), Sofia.adminOperationNotDisabled(name));
        }
    }
}