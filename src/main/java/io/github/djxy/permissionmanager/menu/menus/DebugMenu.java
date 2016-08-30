package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.logger.LoggerMode;
import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

/**
 * Created by Samuel on 2016-08-25.
 */
public class DebugMenu extends Menu {

    public DebugMenu(Player player, Translator translator, Menu from) {
        super(player, translator, from);
    }

    @Override
    public void render(List<Text> lines) {
        User user = UserCollection.instance.get(player);

        this.title = createTitle(translator.getTranslation(UserCollection.instance.get(player).getLanguage(), "menu_main_debug"));

        lines.add(
                Text.of(
                        translator.getTranslation(user.getLanguage(), "menu_debug_current_state"), " ",
                        getDebugStateOn(),
                        TextColors.WHITE, "/",
                        getDebugStateOff()
                )
        );
    }

    private Text getDebugStateOn(){
        User user = UserCollection.instance.get(player);

        if(Logger.getLoggerMode() != LoggerMode.NO_LOG)
            return Text.of(TextColors.YELLOW, translator.getTranslation(user.getLanguage(), "debug_state_on"));
        else
            return Text.of(
                    TextColors.GRAY,
                    runCommandAndRefreshMenuTextAction("pm debug on"),
                    translator.getTranslation(user.getLanguage(), "debug_state_on")
            );
    }

    private Text getDebugStateOff(){
        User user = UserCollection.instance.get(player);

        if(Logger.getLoggerMode() == LoggerMode.NO_LOG)
            return Text.of(TextColors.YELLOW, translator.getTranslation(user.getLanguage(), "debug_state_off"));
        else
            return Text.of(TextColors.GRAY, runCommandAndRefreshMenuTextAction("pm debug off"), translator.getTranslation(user.getLanguage(), "debug_state_off"));
    }

}
