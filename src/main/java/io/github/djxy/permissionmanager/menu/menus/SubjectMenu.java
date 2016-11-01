package io.github.djxy.permissionmanager.menu.menus;

import com.google.common.collect.Sets;
import io.github.djxy.permissionmanager.menu.Menu;
import io.github.djxy.permissionmanager.menu.tabs.WorldContextTab;
import io.github.djxy.permissionmanager.menu.tabs.WorldTab;
import io.github.djxy.permissionmanager.subjects.Subject;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import java.util.List;

/**
 * Created by Samuel on 2016-08-26.
 */
public abstract class SubjectMenu extends Menu {

    private final Subject subject;
    private final String subjectCollectionName;
    private final WorldTab permissionAddWorldTab;
    private final WorldTab permissionDenyWorldTab;
    private final WorldContextTab permissionRemoveWorldTab;
    private final WorldTab optionAddWorldTab;
    private final WorldContextTab optionSetWorldTab;
    private final WorldContextTab optionRemoveWorldTab;
    private final WorldTab groupAddWorldTab;
    private final WorldContextTab groupRemoveWorldTab;
    protected final String identifier;

    public SubjectMenu(Player player, Translator translator, Menu from, String identifier, String subjectCollectionName, Subject subject) {
        super(player, translator, from);
        this.identifier = identifier;
        this.subjectCollectionName = subjectCollectionName;
        this.subject = subject;
        this.title = createTitle(identifier);
        User user = UserCollection.instance.get(player);

        this.permissionAddWorldTab = new WorldTab(this, translator.getTranslation(user.getLanguage(), "menu_subject_add_permission_world"), "pm "+subjectCollectionName+" "+identifier+" set permission <PERMISSION> #");
        this.permissionDenyWorldTab = new WorldTab(this, translator.getTranslation(user.getLanguage(), "menu_subject_deny_permission_world"), "pm "+subjectCollectionName+" "+identifier+" deny permission <PERMISSION> #");
        this.permissionRemoveWorldTab = new WorldContextTab(this, translator.getTranslation(user.getLanguage(), "menu_subject_remove_permission_world"), subject, world -> {
            new PermissionMenuList(player, translator, this, subject.getSubjectData().getPermissions(SubjectData.GLOBAL_CONTEXT), permission -> {
                runCommandAndRefreshMenu("pm " + subjectCollectionName + " " + identifier + " remove permission " + permission + " " + world);
            }).sendToPlayer();
        });
        this.optionAddWorldTab = new WorldTab(this, translator.getTranslation(user.getLanguage(), "menu_subject_add_option_world"), "pm "+subjectCollectionName+" "+identifier+" set option <OPTION> <VALUE> #");
        this.optionSetWorldTab = new WorldContextTab(this, translator.getTranslation(user.getLanguage(), "menu_subject_set_option_world"), subject, world -> {
            new OptionMenuList(player, translator, this, subject.getSubjectData().getOptions(Sets.newHashSet(new Context(Context.WORLD_KEY, world))), false, "pm "+subjectCollectionName+" "+identifier+" set option # "+world).sendToPlayer();
        });
        this.optionRemoveWorldTab = new WorldContextTab(this, translator.getTranslation(user.getLanguage(), "menu_subject_remove_option_world"), subject, world -> {
            new OptionMenuList(player, translator, this, subject.getSubjectData().getOptions(Sets.newHashSet(new Context(Context.WORLD_KEY, world))), true, option -> {
                runCommandAndRefreshMenu("pm " + subjectCollectionName + " " + identifier + " remove option " + option + " " + world);
            }).sendToPlayer();
        });
        this.groupAddWorldTab = new WorldTab(this, translator.getTranslation(user.getLanguage(), "menu_subject_add_group_world"), world -> {
            new GroupMenuList(player, translator, this, group -> {
                runCommandAndRefreshMenu("pm " + subjectCollectionName + " " + identifier + " add group " + group + " " + world);
            }).sendToPlayer();
        });
        this.groupRemoveWorldTab = new WorldContextTab(this, translator.getTranslation(user.getLanguage(), "menu_subject_remove_group_world"), subject, world -> {
            new SubjectGroupMenuList(player, translator, this, subject.getParents(Sets.newHashSet(new Context(Context.WORLD_KEY, world))), group -> {
                runCommandAndRefreshMenu("pm " + subjectCollectionName + " " + identifier + " remove group " + group + " " + world);
            }).sendToPlayer();
        });
    }

    @Override
    public void render(List<Text> lines) {
        User user = UserCollection.instance.get(player);

        lines.add(createOption(suggestCommand("pm " + subjectCollectionName + " " + identifier + " set permission <PERMISSION>"), translator.getTranslation(user.getLanguage(), "menu_subject_add_permission")));
        permissionAddWorldTab.render("", lines);

        lines.add(createOption(suggestCommand("pm " + subjectCollectionName + " " + identifier + " deny permission <PERMISSION>"), translator.getTranslation(user.getLanguage(), "menu_subject_deny_permission")));
        permissionDenyWorldTab.render("", lines);

        lines.add(createOption(TextActions.executeCallback(source -> {
            new PermissionMenuList(player, translator, this, subject.getSubjectData().getPermissions(SubjectData.GLOBAL_CONTEXT), permission -> {
                runCommandAndRefreshMenu("pm " + subjectCollectionName + " " + identifier + " remove permission " + permission);
            }).sendToPlayer();
        }), translator.getTranslation(user.getLanguage(), "menu_subject_remove_permission")));
        permissionRemoveWorldTab.render("", lines);

        lines.add(createOption(suggestCommand("pm " + subjectCollectionName + " " + identifier + " set option <OPTION> <VALUE>"), translator.getTranslation(user.getLanguage(), "menu_subject_add_option")));
        optionAddWorldTab.render("", lines);

        lines.add(createOption(TextActions.executeCallback(source -> {
            new OptionMenuList(player, translator, this, subject.getSubjectData().getOptions(SubjectData.GLOBAL_CONTEXT), false, "pm " + subjectCollectionName + " " + identifier + " set option #").sendToPlayer();
        }), translator.getTranslation(user.getLanguage(), "menu_subject_set_option")));
        optionSetWorldTab.render("", lines);

        lines.add(createOption(TextActions.executeCallback(source -> {
            new OptionMenuList(player, translator, this, subject.getSubjectData().getOptions(Sets.newHashSet(SubjectData.GLOBAL_CONTEXT)), true, option -> {
                runCommandAndRefreshMenu("pm " + subjectCollectionName + " " + identifier + " remove option " + option);
            }).sendToPlayer();
        }), translator.getTranslation(user.getLanguage(), "menu_subject_remove_option")));
        optionRemoveWorldTab.render("", lines);

        lines.add(createOption(
                goToMenuListTextAction(GroupMenuList.class, group -> {
                    runCommandAndRefreshMenu("pm " + subjectCollectionName + " " + identifier + " add group " + group);
                }),
                translator.getTranslation(user.getLanguage(), "menu_subject_add_group")
        ));
        groupAddWorldTab.render("", lines);

        lines.add(createOption(TextActions.executeCallback(source -> {
            new SubjectGroupMenuList(player, translator, this, subject.getParents(SubjectData.GLOBAL_CONTEXT), group -> {
                runCommandAndRefreshMenu("pm " + subjectCollectionName + " " + identifier + " remove group " + group);
            }).sendToPlayer();
        }), translator.getTranslation(user.getLanguage(), "menu_subject_remove_group")));
        groupRemoveWorldTab.render("", lines);

        lines.add(createOption(TextActions.executeCallback(source -> {
            runCommandAndRefreshMenu("pm save " + subjectCollectionName + " " + identifier);
        }), translator.getTranslation(user.getLanguage(), "menu_subject_save")));

        lines.add(createOption(TextActions.executeCallback(source -> {
            runCommandAndRefreshMenu("pm load " + subjectCollectionName + " " + identifier);
        }), translator.getTranslation(user.getLanguage(), "menu_subject_load")));
    }

}
