package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;

/**
 * Created by Samuel on 2016-08-25.
 */
public class UsersMenu extends SubjectsMenu {

    public UsersMenu(Player player, Translator translator, Menu from) {
        super(player, translator, from, "users", UserMenuList.class, UserMenu.class);
    }

    @Override
    public void render(List<Text> lines) {
        this.title = createTitle(translator.getTranslation(UserCollection.instance.get(player).getLanguage(), "menu_main_users"));
        super.render(lines);
    }
}
