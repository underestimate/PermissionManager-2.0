package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

/**
 * Created by Samuel on 2016-08-25.
 */
public class GroupsMenu extends SubjectsMenu {

    public GroupsMenu(Player player, Translator translator, Menu from) {
        super(player, translator, from, "groups", GroupMenuList.class, GroupMenu.class);
    }

    @Override
    public void render(List<Text> lines) {
        User user = UserCollection.instance.get(player);

        this.title = createTitle(translator.getTranslation(UserCollection.instance.get(player).getLanguage(), "menu_main_groups"));

        lines.add(
                Text.of("- "+translator.getTranslation(user.getLanguage(), "menu_groups_default_group")+" ",
                        TextColors.YELLOW,
                        goToMenuListTextAction(GroupMenuList.class, identifier -> {
                            runCommandAndRefreshMenu("pm default group " + identifier);
                        }),
                        GroupCollection.instance.getDefaults().getIdentifier()
                )
        );
        lines.add(
                createOption(suggestCommand("pm create group <GROUP NAME>"),
                        translator.getTranslation(user.getLanguage(), "menu_groups_create")
                )
        );
        lines.add(
                createOption(
                        goToMenuListTextAction(
                                GroupMenuList.class,
                                identifier -> {
                                    Sponge.getCommandManager().process(player, "pm delete group " + identifier);
                                }
                        ),
                        translator.getTranslation(user.getLanguage(), "menu_groups_delete")
                )
        );
        lines.add(
                createOption(
                        goToMenuListTextAction(GroupMenuList.class, "pm rename group # <NEW NAME>"),
                        translator.getTranslation(user.getLanguage(), "menu_groups_rename")
                )
        );

        super.render(lines);
    }

}
