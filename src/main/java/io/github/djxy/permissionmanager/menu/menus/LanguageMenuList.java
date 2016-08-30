package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.language.Language;
import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-08-28.
 */
public class LanguageMenuList extends MenuList {

    public LanguageMenuList(Player player, Translator translator, Menu from, String commandSuggested) {
        super(player, translator, from, commandSuggested);
    }

    public LanguageMenuList(Player player, Translator translator, Menu from, Consumer<String> callback) {
        super(player, translator, from, callback);
    }

    @Override
    public void render(List<Text> lines) {
        for(Language language : Language.getLanguages())
            lines.add(createOption(callback(language.getName()), language.getName()));
    }

}
