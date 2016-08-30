package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.promotion.Promotion;
import io.github.djxy.permissionmanager.promotion.Promotions;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-08-28.
 */
public class PromotionMenuList extends MenuList {

    public PromotionMenuList(Player player, Translator translator, Menu from, String commandSuggested) {
        super(player, translator, from, commandSuggested);
    }

    public PromotionMenuList(Player player, Translator translator, Menu from, Consumer<String> callback) {
        super(player, translator, from, callback);
    }

    @Override
    public void render(List<Text> lines) {
        for(Promotion promotion : Promotions.instance.getPromotions())
            lines.add(createOption(callback(promotion.getName()), promotion.getName()));
    }

}
