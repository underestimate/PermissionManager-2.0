package io.github.djxy.customcommands;

import io.github.djxy.customcommands.parsers.Parser;

import java.util.HashMap;

/**
 * Created by Samuel on 2016-07-28.
 */
public final class Command {

    private HashMap<String, Parser> parsers = new HashMap<>();
    private CommandExecutor commandExecutor;
    private String permission;
    private String command[];
    private String alias;

    protected Command() {
    }

    protected String[] getCommand() {
        return command;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    protected void setCommand(String[] command) {
        this.command = command;
    }

    protected HashMap<String, Parser> getParsers() {
        return parsers;
    }

    protected void setParsers(HashMap<String, Parser> parsers) {
        this.parsers = parsers;
    }

    protected CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    protected void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    protected String getPermission() {
        return permission;
    }

    protected void setPermission(String permission) {
        this.permission = permission;
    }

}
