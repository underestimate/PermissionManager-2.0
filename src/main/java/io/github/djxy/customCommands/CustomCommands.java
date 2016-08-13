package io.github.djxy.customCommands;

import com.google.common.base.Preconditions;
import io.github.djxy.customCommands.annotations.CustomCommand;
import io.github.djxy.customCommands.annotations.CustomParser;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-06-01.
 */
@Plugin(id = "customcommands", name = "CustomCommands", version = "1.0", authors = {"Djxy"})
public class CustomCommands {

    private static final ConcurrentHashMap<String,Node> nodes = new ConcurrentHashMap<>();
    private static CustomCommands instance;

    public static CustomCommands getInstance() {
        return instance;
    }

    @Listener
    public void onGameConstructionEvent(GameConstructionEvent event){
        instance = this;

        for(PluginContainer plugin : Sponge.getPluginManager().getPlugins()) {
            if (plugin.getInstance().isPresent())
                registerObject(plugin.getInstance().get());
        }
    }

    public static void addCommand(Command command){
        Preconditions.checkNotNull(command);

        if(!nodes.containsKey(command.getAlias())) {
            nodes.put(command.getAlias(), new Node("", new String[0], new HashMap<>()));
            System.out.println(instance);
            Sponge.getCommandManager().register(instance, new CommandCallable(command.getAlias()), command.getAlias());
        }
        else if(nodes.get(command.getAlias()).get(command.getCommand(), 0) != null && nodes.get(command.getAlias()).get(command.getCommand(), 0).getCommandExecutor() != null)
            return;

        Node node = nodes.get(command.getAlias());

        node.add(command.getCommand(), 0, command.getParsers());

        node = node.get(command.getCommand(), 0);

        node.setCommandExecutor(command.getCommandExecutor());
        node.setPermission(command.getPermission());
    }

    public static void registerObject(final Object obj) {
        Preconditions.checkNotNull(obj);

        for(Method method : obj.getClass().getMethods()) {
            CustomCommand customCommand = method.getAnnotation(CustomCommand.class);

            if(customCommand != null && !Modifier.isStatic(method.getModifiers())) {
                Parameter parameters[] = method.getParameters();

                if(parameters.length == 2){
                    if (parameters[0].getType().isAssignableFrom(CommandSource.class) && parameters[1].getType().isAssignableFrom(Map.class)) {
                        try {
                            registerCommand(customCommand, new CommandExecutor() {
                                @Override
                                public void execute(CommandSource source, Map<String, Object> values) {
                                    try {
                                        method.invoke(obj, source, values);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.getCause().printStackTrace();
                                    }
                                }
                            });
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static void registerCommand(CustomCommand customCommand, CommandExecutor commandExecutor) throws IllegalAccessException, InstantiationException {
        CommandBuilder builder = CommandBuilder.builder();

        builder.setCommand(customCommand.command());
        builder.setPermission(customCommand.permission().isEmpty() ? null : customCommand.permission());
        builder.setCommandExecutor(commandExecutor);

        for(CustomParser customParser : customCommand.parsers())
            builder.addParser(customParser.argument(), customParser.parser().newInstance());

        CustomCommands.addCommand(builder.build());
    }

    private static class CommandCallable implements org.spongepowered.api.command.CommandCallable {

        private final String alias;

        public CommandCallable(String alias) {
            this.alias = alias;
        }

        @Override
        public CommandResult process(CommandSource commandSource, String s) throws CommandException {
            String args[] = Util.split(alias + " " + s);

            if(args.length == 0)
                return CommandResult.empty();

            if(!nodes.containsKey(args[0]))
                return CommandResult.empty();

            Node node = nodes.get(args[0]).get(args, 0);

            if(node == null || node.getCommandExecutor() == null)
                throw new CommandException(Text.of(TextColors.RED, "This is not a command."));

            if(node.getPermission() != null && !commandSource.hasPermission(node.getPermission()))
                throw new CommandException(Text.of(TextColors.RED, "You don't have the permission to do this command."));

            Map<String, Object> values = node.getValues(new LinkedHashMap<>(), args);

            for(String key : values.keySet())
                if(values.get(key) == null)
                    throw new CommandException(Text.of(TextColors.RED, "The value '"+key+"' isn't valid."));

            node.getCommandExecutor().execute(commandSource, values);

            return CommandResult.success();
        }

        @Override
        public List<String> getSuggestions(CommandSource commandSource, String s, Location<World> location) throws CommandException {
            String args[] = Util.split(alias + " " + s);

            if(args.length == 0)
                return new ArrayList<>();

            if(!nodes.containsKey(args[0]))
                return new ArrayList<>();

            return nodes.get(args[0]).getSuggestions(args, 0);
        }

        @Override
        public boolean testPermission(CommandSource commandSource) {
            return true;
        }

        @Override
        public Optional<Text> getShortDescription(CommandSource commandSource) {
            return Optional.empty();
        }

        @Override
        public Optional<Text> getHelp(CommandSource commandSource) {
            return Optional.empty();
        }

        @Override
        public Text getUsage(CommandSource commandSource) {
            return Text.of();
        }

    }
}
