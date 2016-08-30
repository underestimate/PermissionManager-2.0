package io.github.djxy.permissionmanager.menu.menus;

import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by Samuel on 2016-08-26.
 */
public abstract class SubjectsMenu extends Menu {

    private final Class<? extends MenuList> menuList;
    private final Class<? extends SubjectMenu> subjectMenu;
    private final String subjectCollectionName;

    public SubjectsMenu(Player player, Translator translator, Menu from, String subjectCollectionName, Class<? extends MenuList> menuList, Class<? extends SubjectMenu> subjectMenu) {
        super(player, translator, from);
        this.subjectCollectionName = subjectCollectionName;
        this.menuList = menuList;
        this.subjectMenu = subjectMenu;
    }

    @Override
    public void render(List<Text> lines) {
        User user = UserCollection.instance.get(player);

        lines.add(
                createOption(
                        goToMenuListTextAction(menuList, identifier -> {
                            goToSubjectMenu(identifier);
                        }),
                        translator.getTranslation(user.getLanguage(), "menu_subject_edit") + " " + translator.getTranslation(user.getLanguage(), "menu_subjects_" + subjectCollectionName + "_single") + "."
                )
        );
        lines.add(
                createOption(
                        runCommandAndRefreshMenuTextAction("pm save " + subjectCollectionName),
                        translator.getTranslation(user.getLanguage(), "menu_subjects_save_all") + " " + translator.getTranslation(user.getLanguage(), "menu_subjects_" + subjectCollectionName + "_plural") + "."
                )
        );
        lines.add(
                createOption(
                        goToMenuListTextAction(menuList, identifier -> {
                            runCommandAndRefreshMenu("pm save " + subjectCollectionName + " " + identifier);
                        }),
                        translator.getTranslation(user.getLanguage(), "menu_subjects_save_subject")+" "+translator.getTranslation(user.getLanguage(), "menu_subjects_"+subjectCollectionName+"_single")+"."
                )
        );
        lines.add(
                createOption(
                        goToMenuListTextAction(menuList, identifier -> {
                            runCommandAndRefreshMenu("pm load " + subjectCollectionName + " " + identifier);
                        }),
                        translator.getTranslation(user.getLanguage(), "menu_subjects_load_subject") + " " + translator.getTranslation(user.getLanguage(), "menu_subjects_" + subjectCollectionName + "_single") + "."
                )
        );
    }

    private void goToSubjectMenu(String identifier){
        try {
            subjectMenu.getConstructor(Player.class, Translator.class, Menu.class, String.class).newInstance(player, translator, this, identifier).sendToPlayer();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
