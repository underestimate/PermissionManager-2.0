package io.github.djxy.permissionmanager.menu.tabs;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.menu.Tab;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-08-26.
 */
public class WorldTab extends Tab {

    private final String commandSuggested;
    private final Consumer<String> callback;

    public WorldTab(Menu menu, String text, String commandSuggested) {
        super(menu);
        this.commandSuggested = "/"+commandSuggested;
        this.callback = null;
        setText(text);
    }

    public WorldTab(Menu menu, String text, Consumer<String> callback) {
        super(menu);
        this.callback = callback;
        this.commandSuggested = null;
        setText(text);
    }

    @Override
    protected void renderContent(String margin, List<Text> lines) {
        for(World world : Sponge.getServer().getWorlds())
            lines.add(Text.of(margin).concat(createOption(callback(world.getName()), world.getName())));
    }

    protected TextAction callback(String value){
        return commandSuggested == null? TextActions.executeCallback(source -> {
            callback.accept(value);
        }):TextActions.suggestCommand(commandSuggested.replace("#", value));
    }

}
