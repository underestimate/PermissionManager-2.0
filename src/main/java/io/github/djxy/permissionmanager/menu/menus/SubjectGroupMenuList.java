package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-08-26.
 */
public class SubjectGroupMenuList extends MenuList {

    private final List<Subject> groups;

    public SubjectGroupMenuList(Player player, Translator translator, Menu from, List<Subject> groups, String commandSuggested) {
        super(player, translator, from, commandSuggested);
        this.groups = groups;
    }

    public SubjectGroupMenuList(Player player, Translator translator, Menu from, List<Subject> groups, Consumer<String> callback) {
        super(player, translator, from, callback);
        this.groups = groups;
    }

    @Override
    public void render(List<Text> lines) {
        for(Subject group : groups)
            lines.add(createOption(callback(group.getIdentifier()), group.getIdentifier()));
    }
}
