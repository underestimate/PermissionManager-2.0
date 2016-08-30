package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-08-26.
 */
public class UserMenuList extends MenuList {

    public UserMenuList(Player player, Translator translator, Menu from, Consumer<String> callback) {
        super(player, translator, from, callback);
    }

    public UserMenuList(Player player, Translator translator, Menu from, String commandSuggested) {
        super(player, translator, from, commandSuggested);
    }

    @Override
    public void render(List<Text> lines) {
        for(GameProfile gameProfile : Sponge.getServer().getGameProfileManager().getCache().getProfiles())
            if(gameProfile.getName().isPresent())
                lines.add(createOption(callback(gameProfile.getName().get()), gameProfile.getName().get()));
    }

}
