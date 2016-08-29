package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.subjects.Subject;
import io.github.djxy.permissionmanager.subjects.group.Group;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

/**
 * Created by Samuel on 2016-08-26.
 */
public class GroupMenu extends SubjectMenu {

    private final Group group;

    public GroupMenu(Player player, Translator translator, Menu from, String identifier) {
        super(player, translator, from, identifier, "groups", (Subject) GroupCollection.instance.get(identifier));
        this.group = (Group) GroupCollection.instance.get(identifier);
    }

    @Override
    public void render(List<Text> lines) {
        User user = UserCollection.instance.get(player);

        lines.add(Text.of("- "+translator.getTranslation(user.getLanguage(), "menu_group_rank")+" ", TextColors.YELLOW, suggestCommand("pm groups "+group.getIdentifier()+" rank <RANK>"), group.getRank()+""));
        super.render(lines);
    }
}
