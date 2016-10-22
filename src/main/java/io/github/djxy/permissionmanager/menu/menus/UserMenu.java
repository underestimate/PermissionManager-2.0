package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.subjects.Subject;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

/**
 * Created by Samuel on 2016-08-26.
 */
public class UserMenu extends SubjectMenu {

    private final User user;

    public UserMenu(Player player, Translator translator, Menu from, String name) {
        super(player, translator, from, name, "users", (Subject) UserCollection.instance.get(Sponge.getServer().getGameProfileManager().getCache().getByName(name).get().getUniqueId().toString()));
        this.user = (User) UserCollection.instance.get(Sponge.getServer().getGameProfileManager().getCache().getByName(name).get().getUniqueId().toString());
    }

    @Override
    public void render(List<Text> lines) {
        User user = UserCollection.instance.get(player);

        lines.add(Text.of("- " + translator.getTranslation(user.getLanguage(), "menu_user_language") + " ", TextColors.YELLOW, goToMenuListTextAction(LanguageMenuList.class, language -> {
            runCommandAndRefreshMenu("pm users "+identifier+" language "+language);
        }), user.getLanguage().getName()));
        lines.add(createOption(goToMenuListTextAction(PromotionMenuList.class, promotion -> {
            runCommandAndRefreshMenu("pm promote "+identifier+" "+promotion);
        }), translator.getTranslation(user.getLanguage(), "menu_user_promote")));
        super.render(lines);
    }

}
