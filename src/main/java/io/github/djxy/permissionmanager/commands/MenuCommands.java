package io.github.djxy.permissionmanager.commands;

import io.github.djxy.customcommands.annotations.CustomCommand;
import io.github.djxy.permissionmanager.menu.menus.MainMenu;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Map;

/**
 * Created by Samuel on 2016-08-29.
 */
public class MenuCommands extends Command {

    public static final String PERMISSION_MENU = "permissionmanager.commands.menu";

    public MenuCommands(Translator translator) {
        super(translator);
    }

    @CustomCommand(
            command = "pm",
            permission = PERMISSION_MENU,
            parsers = {}
    )
    public void openMenu1(CommandSource source, Map<String, Object> values) {
        if(source instanceof Player)
            new MainMenu((Player) source, translator).sendToPlayer();
    }

    @CustomCommand(
            command = "pm menu",
            permission = PERMISSION_MENU,
            parsers = {}
    )
    public void openMenu2(CommandSource source, Map<String, Object> values) {
        if(source instanceof Player)
            new MainMenu((Player) source, translator).sendToPlayer();
    }

}
