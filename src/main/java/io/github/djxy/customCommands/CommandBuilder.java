package io.github.djxy.customcommands;

import io.github.djxy.customcommands.parsers.Parser;

import java.util.HashMap;

/**
 * Created by Samuel on 2016-07-28.
 */
public final class CommandBuilder {

    public static CommandBuilder builder(){
        return new CommandBuilder();
    }

    private final HashMap<String, Parser> parsers = new HashMap<>();
    private CommandExecutor commandExecutor;
    private String permission;
    private String command;

    private CommandBuilder() {
    }

    public CommandBuilder setCommand(String command) {
        this.command = command;
        return this;
    }

    public CommandBuilder setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    public CommandBuilder addParser(String argument, Parser parser){
        parsers.put(argument, parser);
        return this;
    }

    public CommandBuilder setPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public Command build(){
        Command command = new Command();
        String commandArgs[] = Util.split(this.command);

        command.setCommandExecutor(commandExecutor);
        command.setParsers(parsers);
        command.setPermission(permission);
        command.setCommand(commandArgs);
        command.setAlias(commandArgs[0]);

        return command;
    }

}
