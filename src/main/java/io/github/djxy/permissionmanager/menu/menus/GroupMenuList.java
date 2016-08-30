package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-08-26.
 */
public class GroupMenuList extends MenuList {

    public GroupMenuList(Player player, Translator translator, Menu from, String commandSuggested) {
        super(player, translator, from, commandSuggested);
    }

    public GroupMenuList(Player player, Translator translator, Menu from, Consumer<String> callback) {
        super(player, translator, from, callback);
    }

    @Override
    public void render(List<Text> lines) {
        for(Subject group : GroupCollection.instance.getAllSubjects())
            lines.add(createOption(callback(group.getIdentifier()), group.getIdentifier()));
    }

}
