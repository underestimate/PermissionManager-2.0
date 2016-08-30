package io.github.djxy.customcommands;

import io.github.djxy.customcommands.parsers.Parser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by Samuel on 2016-07-27.
 */
public final class Node {

    private static final String ARGUMENT_SYMBOL = "#";
    private static final Pattern ONE_ARGUMENT = Pattern.compile("^"+ARGUMENT_SYMBOL+"\\w+$");
    private static final Pattern MULTIPLE_ARGUMENT = Pattern.compile("^"+ARGUMENT_SYMBOL+"\\w+\\.\\.\\.$");

    private final ConcurrentHashMap<String, Node> nodes = new ConcurrentHashMap<>();
    private final LinkedHashMap<Integer,ArgumentEntry> argumentEntries = new LinkedHashMap<>();
    private final int index;
    private final String name;
    private final boolean isMultipleArgument;
    private final Node parent;
    private String permission;
    private CommandExecutor commandExecutor;
    private Parser parser;

    public Node(String arg, String[] command, Map<String,Parser> parsers) {
        this(0, arg, command, parsers, null);
    }

    private Node(int index, String arg, String[] command, Map<String,Parser> parsers, Node parent) {
        this.isMultipleArgument = MULTIPLE_ARGUMENT.matcher(arg).find();
        this.name = parseName(arg);
        this.parser = this.isMultipleArgument?new MultipleArgumentParser():new OneArgumentParser();
        this.index = index;
        this.parent = parent;

        setArgumentEntries(command);
        setParsers(parsers);
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getSuggestions(String command){
        return getNodeFromStart().getSuggestions(Util.split(command), 0);
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public Map<String,Object> getValues(String command){
        return getValues(new HashMap<>(), Util.split(command));
    }

    protected void setPermission(String permission) {
        this.permission = permission;
    }

    protected List<String> getSuggestions(String[] args, int index){
        if(!isMultipleArgument && args.length > 0 && index < args.length)
            if(nodes.containsKey(args[index]))
                return nodes.get(args[index]).getSuggestions(args, index + 1);
            else if(nodes.containsKey(ARGUMENT_SYMBOL))
                return nodes.get(ARGUMENT_SYMBOL).getSuggestions(args, index + 1);
            else
                return parser.getSuggestions(args[index]);
        else
            return parser.getSuggestions(args.length == index ? args[index - 1] : args[index]);
    }

    protected void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    protected Map<String,Object> getValues(Map<String,Object> values, String[] args){
        for(Map.Entry pairs : argumentEntries.entrySet()) {
            int argIndex = (int) pairs.getKey();

            if ((isMultipleArgument && argIndex < index) || !isMultipleArgument)
                values.put(((ArgumentEntry) pairs.getValue()).name, args[argIndex]);
        }

        if(isMultipleArgument) {
            String arg = "";

            for (int i = index; i < args.length; i++)
                arg += args[i]+" ";

            values.put(argumentEntries.get(index).name, arg.trim());
        }

        for(ArgumentEntry entry : argumentEntries.values())
            if (values.containsKey(entry.name) && entry.parser != null)
                values.put(entry.name, entry.parser.parse((String) values.get(entry.name)));

        return values;
    }

    protected Node get(String[] args, int index){
        if(!isMultipleArgument && args.length > 0 && index < args.length)
            if(nodes.containsKey(args[index]))
                return nodes.get(args[index]).get(args, index+1);
            else if(nodes.containsKey(ARGUMENT_SYMBOL))
                return nodes.get(ARGUMENT_SYMBOL).get(args, index+1);
            else
                return null;
        else
            return this;
    }

    protected void add(String[] args, int index, Map<String,Parser> parsers) {
        if(args.length > 0 && index < args.length){
            String arg = args[index].startsWith(ARGUMENT_SYMBOL)?ARGUMENT_SYMBOL:args[index];

            if(!nodes.containsKey(arg)){
                Node node = new Node(index, args[index], Arrays.copyOfRange(args, 0, index + 1, String[].class), parsers, this);

                if(args[index].startsWith(ARGUMENT_SYMBOL))
                    nodes.put(arg, node);
                else
                    nodes.put(arg, node);
            }

            nodes.get(arg).add(args, index+1, parsers);
        }
    }

    private void setArgumentEntries(String[] command){
        for(int i = 0; i < command.length; i++) {
            String argument = command[i];

            if (ONE_ARGUMENT.matcher(argument).find())
                argumentEntries.put(i, new ArgumentEntry(argument.substring(1)));
            else if(MULTIPLE_ARGUMENT.matcher(argument).find())
                argumentEntries.put(i, new ArgumentEntry(argument.substring(1, argument.length() - 3)));
        }
    }

    private void setParsers(Map<String,Parser> parsers){
        for(ArgumentEntry entry : argumentEntries.values())
            if (parsers.containsKey(entry.name))
                entry.parser = parsers.get(entry.name);

        if(name != null && parsers.containsKey(name))
            this.parser = parsers.get(name);
    }

    private String parseName(String arg){
        if(arg.startsWith(ARGUMENT_SYMBOL)){
            if (ONE_ARGUMENT.matcher(arg).find())
                return arg.substring(1);
            else if(MULTIPLE_ARGUMENT.matcher(arg).find())
                return arg.substring(1, arg.length() - 3);
            else
                return null;
        }
        else
            return null;
    }

    private Node getNodeFromStart(){
        Node startFrom = this;

        while(startFrom.parent != null)
            startFrom = startFrom.parent;

        return startFrom;
    }

    private class ArgumentEntry {

        private String name;
        private Parser parser;

        public ArgumentEntry(String name) {
            this.name = name;
        }

    }

    private class OneArgumentParser extends Parser<String> {

        @Override
        public String parse(String value) {
            return "";
        }

        @Override
        public List<String> getSuggestions(String value) {
            return getSuggestions(Node.this.nodes.keySet(), value);
        }

    }

    private class MultipleArgumentParser extends Parser<String> {

        @Override
        public String parse(String value) {
            return "";
        }

        @Override
        public List<String> getSuggestions(String value) {
            return new ArrayList<>();
        }

    }

}
