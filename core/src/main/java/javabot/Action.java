package javabot;

public class Action extends Message {
    public Action(String dest, IrcEvent evt, String value) {
        super(dest, evt, value);
    }

    @Override
    public void send(Javabot bot) {
//        bot.postAction(this);
    }
}