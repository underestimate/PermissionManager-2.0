package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.List;

/**
 * Created by Samuel on 2016-08-29.
 */
public class PromotionMenu extends Menu {

    public PromotionMenu(Player player, Translator translator) {
        super(player, translator);
    }

    public PromotionMenu(Player player, Translator translator, Menu from) {
        super(player, translator, from);
    }

    @Override
    public void render(List<Text> lines) {
        User user = UserCollection.instance.get(player);

        this.title = createTitle(translator.getTranslation(user.getLanguage(), "menu_main_promotion"));

        lines.add(createOption(suggestCommand("pm create promotion <PROMOTION>"), translator.getTranslation(user.getLanguage(), "menu_promotion_create")));
        lines.add(createOption(goToMenuListTextAction(PromotionMenuList.class, "pm rename promotion # <NEW NAME>"), translator.getTranslation(user.getLanguage(), "menu_promotion_rename")));
        lines.add(createOption(goToMenuListTextAction(PromotionMenuList.class, promotion -> {
            Sponge.getCommandManager().process(player, "pm delete promotion " + promotion);
        }), translator.getTranslation(user.getLanguage(), "menu_promotion_delete")));
        lines.add(createOption(goToMenuListTextAction(PromotionMenuList.class, promotion -> {
            runCommandAndRefreshMenu("pm load promotions "+promotion);
        }), translator.getTranslation(user.getLanguage(), "menu_promotion_load")));
        lines.add(createOption(TextActions.executeCallback(source -> {
            runCommandAndRefreshMenu("pm load promotions");
        }), translator.getTranslation(user.getLanguage(), "menu_promotion_load_all")));
        lines.add(createOption(goToMenuListTextAction(PromotionMenuList.class, promotion -> {
            runCommandAndRefreshMenu("pm save promotions "+promotion);
        }), translator.getTranslation(user.getLanguage(), "menu_promotion_save")));
        lines.add(createOption(TextActions.executeCallback(source -> {
            runCommandAndRefreshMenu("pm save promotions");
        }), translator.getTranslation(user.getLanguage(), "menu_promotion_save_all")));
        lines.add(createOption(goToMenuListTextAction(PromotionMenuList.class, promotion -> {
            new UserMenuList(player, translator, this, userToApply -> {
                runCommandAndRefreshMenu("pm promote "+userToApply+" "+promotion);
            }).sendToPlayer();
        }), translator.getTranslation(user.getLanguage(), "menu_promotion_apply_promotion")));
    }

}
