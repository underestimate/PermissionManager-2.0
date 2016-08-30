package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;

/**
 * Created by Samuel on 2016-08-25.
 */
public class MainMenu extends Menu {

    public MainMenu(Player player, Translator translator) {
        super(player, translator);
        title = Text.of(TextActions.executeCallback(source -> {sendToPlayer();})).concat(TextSerializers.FORMATTING_CODE.deserialize("&f&6Permission&l&4M&r&f"));
    }

    @Override
    public void render(List<Text> lines) {
        User user = UserCollection.instance.get(player);

        lines.add(createOption(goToMenuTextAction(GroupsMenu.class), translator.getTranslation(user.getLanguage(), "menu_main_groups")));
        lines.add(createOption(goToMenuTextAction(UsersMenu.class), translator.getTranslation(user.getLanguage(), "menu_main_users")));
        lines.add(createOption(goToMenuTextAction(DebugMenu.class), translator.getTranslation(user.getLanguage(), "menu_main_debug")));
        lines.add(createOption(goToMenuTextAction(PromotionMenu.class), translator.getTranslation(user.getLanguage(), "menu_main_promotion")));
    }

}
