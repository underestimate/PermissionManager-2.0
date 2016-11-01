package io.github.djxy.permissionmanager.commands;

import com.google.common.collect.Sets;
import io.github.djxy.customcommands.CommandBuilder;
import io.github.djxy.customcommands.CustomCommands;
import io.github.djxy.customcommands.parsers.Parser;
import io.github.djxy.permissionmanager.commands.parsers.GroupParser;
import io.github.djxy.permissionmanager.commands.parsers.WorldParser;
import io.github.djxy.permissionmanager.menu.menus.GroupMenu;
import io.github.djxy.permissionmanager.menu.menus.MainMenu;
import io.github.djxy.permissionmanager.menu.menus.UserMenu;
import io.github.djxy.permissionmanager.subjects.Subject;
import io.github.djxy.permissionmanager.subjects.SubjectCollection;
import io.github.djxy.permissionmanager.subjects.group.Group;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.action.TextAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.World;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Samuel on 2016-08-24.
 */
public abstract class SubjectCommands extends Command {

    private final static long MAX_TIME_ACTION_AVAILABLE = 30000;

    private final SubjectCollection subjectCollection;
    private final String subjectCollectionName;
    private final String subjectName;
    private final Parser<? extends Subject> subjectParser;

    abstract String getSubjectName(Subject subject);

    public SubjectCommands(Translator translator, SubjectCollection subjectCollection, String subjectCollectionName, String subjectName, Parser<? extends Subject> subjectParser) {
        super(translator);
        this.subjectCollection = subjectCollection;
        this.subjectCollectionName = subjectCollectionName;
        this.subjectName = subjectName;
        this.subjectParser = subjectParser;
    }

    /**
     * Register the commands created dynamically
     * @return
     */
    public SubjectCommands register(){
        CustomCommands.addCommand(setPermissionGlobal());
        CustomCommands.addCommand(setPermissionWorld());
        CustomCommands.addCommand(denyPermissionGlobal());
        CustomCommands.addCommand(denyPermissionWorld());
        CustomCommands.addCommand(removePermissionGlobal());
        CustomCommands.addCommand(removePermissionWorld());
        CustomCommands.addCommand(clearPermissionsGlobal());
        CustomCommands.addCommand(clearPermissionsWorld());

        CustomCommands.addCommand(addGroupGlobal());
        CustomCommands.addCommand(addGroupWorld());
        CustomCommands.addCommand(removeGroupGlobal());
        CustomCommands.addCommand(removeGroupWorld());
        CustomCommands.addCommand(clearGroupsGlobal());
        CustomCommands.addCommand(clearGroupsWorld());

        CustomCommands.addCommand(setOptionGlobal());
        CustomCommands.addCommand(setOptionWorld());
        CustomCommands.addCommand(removeOptionGlobal());
        CustomCommands.addCommand(removeOptionWorld());
        CustomCommands.addCommand(clearOptionsGlobal());
        CustomCommands.addCommand(clearOptionsWorld());

        CustomCommands.addCommand(loadSubject());

        CustomCommands.addCommand(saveSubject());
        CustomCommands.addCommand(saveSubjects());

        CustomCommands.addCommand(openSubjectMenu());

        return this;
    }

    private io.github.djxy.customcommands.Command openSubjectMenu(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName)
                .setPermission(MenuCommands.PERMISSION_MENU + "." + subjectCollectionName)
                .addParser(subjectName, subjectParser)
                .setCommandExecutor((source, values) -> {
                    if (source instanceof Player) {
                        MainMenu mainMenu = new MainMenu((Player) source, translator);

                        if(subjectCollection == UserCollection.instance)
                            new UserMenu((Player) source, translator, mainMenu, Sponge.getServer().getGameProfileManager().getCache().getById(UUID.fromString(((Subject) values.get(subjectName)).getIdentifier())).get().getName().get()).sendToPlayer();
                        else if(subjectCollection == GroupCollection.instance)
                            new GroupMenu((Player) source, translator, mainMenu, ((Subject) values.get(subjectName)).getIdentifier()).sendToPlayer();
                    }
                })
                .build();
    }

    private io.github.djxy.customcommands.Command loadSubject(){
        return CommandBuilder.builder()
                .setCommand("pm load " + subjectCollectionName + " #" + subjectName)
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".load")
                .addParser(subjectName, subjectParser)
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);

                    if (subjectCollection.load(subject.getIdentifier())) {
                        source.sendMessage(
                                parser.parse(
                                        translator.getTranslation(getLanguage(source), subjectName + "_loaded"),
                                        EMPTY_MAP,
                                        createVariableMap(subjectName, getSubjectName(subject)),
                                        EMPTY_MAP
                                )
                        );
                    }
                    else
                        parser.parse(translator.getTranslation(getLanguage(source), "loading_error"), EMPTY_MAP, EMPTY_MAP, EMPTY_MAP);
                })
                .build();
    }

    private io.github.djxy.customcommands.Command saveSubject(){
        return CommandBuilder.builder()
                .setCommand("pm save " + subjectCollectionName + " #" + subjectName)
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".save")
                .addParser(subjectName, subjectParser)
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);

                    try {
                        subjectCollection.save(subject.getIdentifier());

                        source.sendMessage(
                                parser.parse(
                                        translator.getTranslation(getLanguage(source), subjectName + "_saved"),
                                        EMPTY_MAP,
                                        createVariableMap(subjectName, getSubjectName(subject)),
                                        EMPTY_MAP
                                )
                        );
                    } catch (Exception e) {
                        parser.parse(translator.getTranslation(getLanguage(source), "saving_error"), EMPTY_MAP, EMPTY_MAP, EMPTY_MAP);
                        e.printStackTrace();
                    }
                })
                .build();
    }

    private io.github.djxy.customcommands.Command saveSubjects(){
        return CommandBuilder.builder()
                .setCommand("pm save " + subjectCollectionName)
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".save")
                .addParser(subjectName, subjectParser)
                .setCommandExecutor((source, values) -> {
                    subjectCollection.save();

                    source.sendMessage(parser.parse(translator.getTranslation(getLanguage(source), subjectName + "_saved_all"), EMPTY_MAP, EMPTY_MAP, EMPTY_MAP));
                })
                .build();
    }

    private io.github.djxy.customcommands.Command setOptionGlobal(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " set option #option #value")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".set.option")
                .addParser(subjectName, subjectParser)
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    subject.getSubjectData().setOption(SubjectData.GLOBAL_CONTEXT, values.get("option").toString(), values.get("value").toString());

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName+"_option_set_global"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "option", values.get("option").toString(),
                                            "value", values.get("value").toString()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command setOptionWorld(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " set option #option #value #world")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".set.option")
                .addParser(subjectName, subjectParser)
                .addParser("world", new WorldParser())
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    World world = (World) values.get("world");
                    subject.getSubjectData().setOption(Sets.newHashSet(new Context(Context.WORLD_KEY, world.getName())), values.get("option").toString(), values.get("value").toString());

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_option_set_context_world"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "world", world.getName(),
                                            "option", values.get("option").toString(),
                                            "value", values.get("value").toString()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command removeOptionGlobal(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " remove option #option")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".remove.option")
                .addParser(subjectName, subjectParser)
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    subject.getSubjectData().setOption(SubjectData.GLOBAL_CONTEXT, values.get("option").toString(), null);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName+"_option_remove_global"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "option", values.get("option").toString()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command removeOptionWorld(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " remove option #option #world")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".remove.option")
                .addParser(subjectName, subjectParser)
                .addParser("world", new WorldParser())
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    World world = (World) values.get("world");

                    subject.getSubjectData().setOption(Sets.newHashSet(new Context(Context.WORLD_KEY, world.getName())), values.get("option").toString(), null);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_option_remove_context_world"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "world", world.getName(),
                                            "option", values.get("option").toString()
                                    ),
                                    EMPTY_MAP
                            )
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command removeGroupGlobal(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " remove group #groupRemoved")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".remove.group")
                .addParser(subjectName, subjectParser)
                .addParser("groupRemoved", new GroupParser())
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    Group group = (Group) values.get("groupRemoved");

                    subject.getSubjectData().removeParent(SubjectData.GLOBAL_CONTEXT, group);
                    
                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName+"_group_remove_global"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "groupRemoved", group.getIdentifier()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command removeGroupWorld(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " remove group #groupRemoved #world")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".remove.group")
                .addParser(subjectName, subjectParser)
                .addParser("groupRemoved", new GroupParser())
                .addParser("world", new WorldParser())
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    World world = (World) values.get("world");
                    Group group = (Group) values.get("groupRemoved");

                    subject.getSubjectData().removeParent(Sets.newHashSet(new Context(Context.WORLD_KEY, world.getName())), group);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_group_remove_context_world"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "world", world.getName(),
                                            "groupRemoved", group.getIdentifier()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command addGroupGlobal(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " add group #newGroup")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".add.group")
                .addParser(subjectName, subjectParser)
                .addParser("newGroup", new GroupParser())
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    Group group = (Group) values.get("newGroup");

                    subject.getSubjectData().addParent(SubjectData.GLOBAL_CONTEXT, group);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName+"_group_add_global"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "newGroup", group.getIdentifier()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command addGroupWorld(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " add group #newGroup #world")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".add.group")
                .addParser(subjectName, subjectParser)
                .addParser("newGroup", new GroupParser())
                .addParser("world", new WorldParser())
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    World world = (World) values.get("world");
                    Group group = (Group) values.get("newGroup");

                    subject.getSubjectData().addParent(Sets.newHashSet(new Context(Context.WORLD_KEY, world.getName())), group);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_group_add_context_world"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "world", world.getName(),
                                            "newGroup", group.getIdentifier()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command clearGroupsGlobal(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " clear groups")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".clear.group")
                .addParser(subjectName, subjectParser)
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_group_clear_global_confirmation"),
                                    createVariableMap("click_confirmation", translator.getTranslation(getLanguage(source), "click_confirmation")),
                                    createVariableMap(subjectName, getSubjectName(subject)),
                                    createVariableMap("click_confirmation", createTextActionCallback(source1 -> {
                                        subject.getSubjectData().clearParents(SubjectData.GLOBAL_CONTEXT);

                                        source.sendMessage(
                                                parser.parse(
                                                        translator.getTranslation(getLanguage(source), subjectName+"_group_clear_global"),
                                                        EMPTY_MAP,
                                                        createVariableMap(subjectName, getSubjectName(subject)),
                                                        EMPTY_MAP
                                                )
                                        );
                                    })))
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command clearGroupsWorld(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " clear groups #world")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".clear.group")
                .addParser(subjectName, subjectParser)
                .addParser("world", new WorldParser())
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    World world = (World) values.get("world");

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_group_clear_context_world_confirmation"),
                                    createVariableMap("click_confirmation", translator.getTranslation(getLanguage(source), "click_confirmation")),
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "world", world.getName()
                                    ),
                                    createVariableMap("click_confirmation", createTextActionCallback(source1 -> {
                                        subject.getSubjectData().clearParents(Sets.newHashSet(new Context(Context.WORLD_KEY, world.getName())));

                                        source.sendMessage(
                                                parser.parse(
                                                        translator.getTranslation(getLanguage(source), subjectName+"_group_clear_context_world"),
                                                        EMPTY_MAP,
                                                        createVariableMap(
                                                                subjectName, getSubjectName(subject),
                                                                "world", world.getName()
                                                        ),
                                                        EMPTY_MAP
                                                )
                                        );
                                    })))
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command clearOptionsGlobal(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " clear options")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".clear.option")
                .addParser(subjectName, subjectParser)
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_option_clear_global_confirmation"),
                                    createVariableMap("click_confirmation", translator.getTranslation(getLanguage(source), "click_confirmation")),
                                    createVariableMap(subjectName, getSubjectName(subject)),
                                    createVariableMap("click_confirmation", createTextActionCallback(source1 -> {
                                        subject.getSubjectData().clearOptions(SubjectData.GLOBAL_CONTEXT);

                                        source.sendMessage(
                                                parser.parse(
                                                        translator.getTranslation(getLanguage(source), subjectName+"_option_clear_global"),
                                                        EMPTY_MAP,
                                                        createVariableMap(subjectName, getSubjectName(subject)),
                                                        EMPTY_MAP
                                                )
                                        );
                                    })))
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command clearOptionsWorld(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " clear options #world")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".clear.option")
                .addParser(subjectName, subjectParser)
                .addParser("world", new WorldParser())
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    World world = (World) values.get("world");

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_option_clear_context_world_confirmation"),
                                    createVariableMap("click_confirmation", translator.getTranslation(getLanguage(source), "click_confirmation")),
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "world", world.getName()
                                    ),
                                    createVariableMap("click_confirmation", createTextActionCallback(source1 -> {
                                        subject.getSubjectData().clearOptions(Sets.newHashSet(new Context(Context.WORLD_KEY, world.getName())));

                                        source.sendMessage(
                                                parser.parse(
                                                        translator.getTranslation(getLanguage(source), subjectName+"_option_clear_context_world"),
                                                        EMPTY_MAP,
                                                        createVariableMap(
                                                                subjectName, getSubjectName(subject),
                                                                "world", world.getName()
                                                        ),
                                                        EMPTY_MAP
                                                )
                                        );
                                    })))
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command clearPermissionsGlobal(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " clear permissions")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".clear.permission")
                .addParser(subjectName, subjectParser)
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_permission_clear_global_confirmation"),
                                    createVariableMap("click_confirmation", translator.getTranslation(getLanguage(source), "click_confirmation")),
                                    createVariableMap(subjectName, getSubjectName(subject)),
                                    createVariableMap("click_confirmation", createTextActionCallback(source1 -> {
                                        subject.getSubjectData().clearPermissions(SubjectData.GLOBAL_CONTEXT);

                                        source.sendMessage(
                                                parser.parse(
                                                        translator.getTranslation(getLanguage(source), subjectName+"_permission_clear_global"),
                                                        EMPTY_MAP,
                                                        createVariableMap(subjectName, getSubjectName(subject)),
                                                        EMPTY_MAP
                                                )
                                        );
                                    })))
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command clearPermissionsWorld(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " clear permissions #world")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".clear.permission")
                .addParser(subjectName, subjectParser)
                .addParser("world", new WorldParser())
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    World world = (World) values.get("world");

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_permission_clear_context_world_confirmation"),
                                    createVariableMap("click_confirmation", translator.getTranslation(getLanguage(source), "click_confirmation")),
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "world", world.getName()
                                    ),
                                    createVariableMap("click_confirmation", createTextActionCallback(source1 -> {
                                        subject.getSubjectData().clearPermissions(Sets.newHashSet(new Context(Context.WORLD_KEY, world.getName())));

                                        source.sendMessage(
                                                parser.parse(
                                                        translator.getTranslation(getLanguage(source), subjectName + "_permission_clear_context_world"),
                                                        EMPTY_MAP,
                                                        createVariableMap(
                                                                subjectName, getSubjectName(subject),
                                                                "world", world.getName()
                                                        ),
                                                        EMPTY_MAP
                                                )
                                        );
                                    })))
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command setPermissionGlobal(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " set permission #permission")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".set.permission")
                .addParser(subjectName, subjectParser)
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    subject.getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, values.get("permission").toString(), Tristate.TRUE);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName+"_permission_set_global"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "permission", values.get("permission").toString()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command setPermissionWorld(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " set permission #permission #world")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".set.permission")
                .addParser(subjectName, subjectParser)
                .addParser("world", new WorldParser())
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    World world = (World) values.get("world");
                    subject.getSubjectData().setPermission(Sets.newHashSet(new Context(Context.WORLD_KEY, world.getName())), values.get("permission").toString(), Tristate.TRUE);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_permission_set_context_world"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "world", world.getName(),
                                            "permission", values.get("permission").toString()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command removePermissionGlobal(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " remove permission #permission")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".remove.permission")
                .addParser(subjectName, subjectParser)
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    subject.getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, values.get("permission").toString(), Tristate.UNDEFINED);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName+"_permission_remove_global"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "permission", values.get("permission").toString()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command removePermissionWorld(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " remove permission #permission #world")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".remove.permission")
                .addParser(subjectName, subjectParser)
                .addParser("world", new WorldParser())
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    World world = (World) values.get("world");
                    subject.getSubjectData().setPermission(Sets.newHashSet(new Context(Context.WORLD_KEY, world.getName())), values.get("permission").toString(), Tristate.UNDEFINED);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_permission_remove_context_world"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "world", world.getName(),
                                            "permission", values.get("permission").toString()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command denyPermissionGlobal(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " deny permission #permission")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".set.permission")
                .addParser(subjectName, subjectParser)
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    subject.getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, values.get("permission").toString(), Tristate.FALSE);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName+"_permission_deny_global"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "permission", values.get("permission").toString()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private io.github.djxy.customcommands.Command denyPermissionWorld(){
        return CommandBuilder.builder()
                .setCommand("pm " + subjectCollectionName + " #" + subjectName + " deny permission #permission #world")
                .setPermission("permissionmanager.commands." + subjectCollectionName + ".set.permission")
                .addParser(subjectName, subjectParser)
                .addParser("world", new WorldParser())
                .setCommandExecutor((source, values) -> {
                    Subject subject = (Subject) values.get(subjectName);
                    World world = (World) values.get("world");
                    subject.getSubjectData().setPermission(Sets.newHashSet(new Context(Context.WORLD_KEY, world.getName())), values.get("permission").toString(), Tristate.FALSE);

                    source.sendMessage(
                            parser.parse(
                                    translator.getTranslation(getLanguage(source), subjectName + "_permission_deny_context_world"),
                                    EMPTY_MAP,
                                    createVariableMap(
                                            subjectName, getSubjectName(subject),
                                            "world", world.getName(),
                                            "permission", values.get("permission").toString()
                                    ),
                                    EMPTY_MAP)
                    );
                })
                .build();
    }

    private TextAction createTextActionCallback(Consumer<CommandSource> consumer){
        long time = System.currentTimeMillis();
        final boolean[] alreadyExecuted = {false};

        return TextActions.executeCallback(source1 -> {
            if(alreadyExecuted[0]){
                source1.sendMessage(parser.parse(translator.getTranslation(getLanguage(source1), "action_already_executed"), EMPTY_MAP, EMPTY_MAP, EMPTY_MAP));
                return;
            }
            if(System.currentTimeMillis() > time+MAX_TIME_ACTION_AVAILABLE){
                source1.sendMessage(parser.parse(translator.getTranslation(getLanguage(source1), "action_no_longer_available"), EMPTY_MAP, EMPTY_MAP, EMPTY_MAP));
                return;
            }

            consumer.accept(source1);
            alreadyExecuted[0] = true;
        });
    }

}
