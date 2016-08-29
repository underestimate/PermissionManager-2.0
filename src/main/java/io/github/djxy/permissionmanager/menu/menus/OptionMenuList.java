package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-08-26.
 */
public class OptionMenuList extends MenuList {

    private final boolean onlyOption;
    private final Map<String,String> options;

    public OptionMenuList(Player player, Translator translator, Menu from, Map<String, String> options, boolean onlyOption, String commandSuggested) {
        super(player, translator, from, commandSuggested);
        this.options = options;
        this.onlyOption = onlyOption;
    }

    public OptionMenuList(Player player, Translator translator, Menu from, Map<String, String> options, boolean onlyOption, Consumer<String> callback) {
        super(player, translator, from, callback);
        this.options = options;
        this.onlyOption = onlyOption;
    }

    @Override
    public void render(List<Text> lines) {
        for(String option : options.keySet())
            lines.add(createOption(callback(onlyOption?option:option+" "+options.get(option)), option+": "+options.get(option)));
    }
}
