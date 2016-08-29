package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;

import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-08-26.
 */
public abstract class MenuList extends Menu {

    private final String commandSuggested;
    private final Consumer<String> callback;

    public MenuList(Player player, Translator translator, Menu from, String commandSuggested) {
        super(player, translator, from);
        this.commandSuggested = "/"+commandSuggested;
        this.callback = null;
    }

    public MenuList(Player player, Translator translator, Menu from, Consumer<String> callback) {
        super(player, translator, from);
        this.commandSuggested = null;
        this.callback = callback;
    }

    protected TextAction callback(String value){
        return commandSuggested == null?TextActions.executeCallback(source -> {callback.accept(value);}):TextActions.suggestCommand(commandSuggested.replace("#", value));
    }

}
