package io.github.djxy.permissionManager;

import io.github.djxy.customCommands.CustomCommands;
import io.github.djxy.customCommands.annotations.CustomCommand;
import io.github.djxy.customCommands.annotations.CustomParser;
import io.github.djxy.customCommands.parsers.BooleanParser;
import io.github.djxy.permissionManager.commands.CreateGroupCommand;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.util.Map;

/**
 * Created by Samuel on 2016-08-07.
 */
@Plugin(id = "permissionmanager", name = "PermissionManager v2", version = "2.0", authors = {"Djxy"})
public class PermissionManager {

    private static PermissionManager instance;

    public static PermissionManager getInstance() {
        return instance;
    }

    @Listener
    public void onGameConstructionEvent(GameConstructionEvent event){
        instance = this;
    }

    @Listener
    public void onGameInitializationEvent(GameInitializationEvent event){
        CustomCommands.registerObject(new CreateGroupCommand());
    }

    @CustomCommand(
            command = "pm set #player #visible #argument...",
            permission = "bob.rtsad.123",
            parsers = {
                    @CustomParser(argument = "player", parser = BooleanParser.class),
                    @CustomParser(argument = "visible", parser = BooleanParser.class)
            }
    )
    public void test(CommandSource source, Map<String,Object> values){
        source.sendMessage(Text.of("Salut test"));
        System.out.println(source.getName());
        System.out.println(values);
    }

}
