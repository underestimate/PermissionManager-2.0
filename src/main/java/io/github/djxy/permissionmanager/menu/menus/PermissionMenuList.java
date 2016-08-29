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
public class PermissionMenuList extends MenuList {

    private final Map<String,Boolean> permissions;

    public PermissionMenuList(Player player, Translator translator, Menu from, Map<String,Boolean> permissions, String commandSuggested) {
        super(player, translator, from, commandSuggested);
        this.permissions = permissions;
    }

    public PermissionMenuList(Player player, Translator translator, Menu from, Map<String,Boolean> permissions, Consumer<String> callback) {
        super(player, translator, from, callback);
        this.permissions = permissions;
    }

    @Override
    public void render(List<Text> lines) {
        for(String permission : permissions.keySet())
            lines.add(createOption(callback(permission), permission));
    }
}
