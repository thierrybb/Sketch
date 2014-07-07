package ca.etsmtl.sketch.graphic.util;

import java.util.ArrayList;

public class ActionTask {

    ArrayList<Command> commands;

    public ActionTask() {
        commands = new ArrayList<Command>();
    }

    public Command pop() {
        if (commands.isEmpty()) {
            return null;
        } else {
            Command c = commands.get(commands.size() - 1);
            commands.remove(c);
            return c;
        }
    }

    public void push(Command c) {
        this.commands.add(c);
    }
}

