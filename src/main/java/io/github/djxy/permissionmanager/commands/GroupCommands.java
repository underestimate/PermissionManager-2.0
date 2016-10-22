package io.github.djxy.permissionmanager.commands;

import io.github.djxy.customcommands.annotations.CustomCommand;
import io.github.djxy.customcommands.annotations.CustomParser;
import io.github.djxy.customcommands.parsers.IntegerParser;
import io.github.djxy.permissionmanager.commands.parsers.GroupParser;
import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierExistException;
import io.github.djxy.permissionmanager.subjects.Subject;
import io.github.djxy.permissionmanager.subjects.group.Group;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;
import io.github.djxy.permissionmanager.translator.Translator;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.action.TextActions;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Samuel on 2016-08-24.
 */
public class GroupCommands extends SubjectCommands {

    private static final String PERMISSION_GROUP_DEFAULT_SET = "permissionmanager.commands.groups.set.default";
    private static final String PERMISSION_GROUP_RANK_SET = "permissionmanager.commands.groups.set.rank";
    private static final String PERMISSION_GROUP_RENAME = "permissionmanager.commands.groups.rename";
    private static final String PERMISSION_GROUP_DELETE = "permissionmanager.commands.groups.delete";
    private static final String PERMISSION_GROUP_CREATE = "permissionmanager.commands.groups.create";
    private static final String PERMISSION_GROUP_LOAD = "permissionmanager.commands.groups.load";

    public GroupCommands(Translator translator) {
        super(translator, GroupCollection.instance, "groups", "group", new GroupParser());
    }

    @CustomCommand(
            command = "pm load groups",
            permission = PERMISSION_GROUP_LOAD,
            parsers = {}
    )
    public void loadGroups(CommandSource source, Map<String, Object> values){
        GroupCollection.instance.load();

        source.sendMessage(
                parser.parse(
                        translator.getTranslation(getLanguage(source), "group_loaded_all"),
                        EMPTY_MAP,
                        EMPTY_MAP,
                        EMPTY_MAP
                )
        );
    }

    @CustomCommand(
            command = "pm default group #group",
            permission = PERMISSION_GROUP_DEFAULT_SET,
            parsers = {
                    @CustomParser(argument = "group", parser = GroupParser.class)
            }
    )
    public void setGroupDefault(CommandSource source, Map<String, Object> values){
        Group group = (Group) values.get("group");

        group.setDefaultGroup(true);

        source.sendMessage(
                parser.parse(
                        translator.getTranslation(getLanguage(source), "group_default_change"),
                        EMPTY_MAP,
                        createVariableMap("group", group.getIdentifier()),
                        EMPTY_MAP
                )
        );
    }

    @CustomCommand(
            command = "pm groups #group rank #rank",
            permission = PERMISSION_GROUP_RANK_SET,
            parsers = {
                    @CustomParser(argument = "group", parser = GroupParser.class),
                    @CustomParser(argument = "rank", parser = IntegerParser.class)
            }
    )
    public void setGroupRank(CommandSource source, Map<String, Object> values){
        Group group = (Group) values.get("group");
        Integer rank = (Integer) values.get("rank");

        group.setRank(rank);
        source.sendMessage(
                parser.parse(
                        translator.getTranslation(getLanguage(source), "group_rank_set"),
                        EMPTY_MAP,
                        createVariableMap(
                                "group", group.getIdentifier(),
                                "rank", rank.toString()
                        ),
                        EMPTY_MAP
                )
        );
    }

    @CustomCommand(
            command = "pm rename group #group #newName",
            permission = PERMISSION_GROUP_RENAME,
            parsers = {@CustomParser(argument = "group", parser = GroupParser.class)}
    )
    public void renameGroup(CommandSource source, Map<String, Object> values) {
        Group group = (Group) values.get("group");

        try {
            String lastName = group.getIdentifier();

            GroupCollection.instance.renameGroup(group, values.get("newName").toString());
            source.sendMessage(
                    parser.parse(
                            translator.getTranslation(getLanguage(source), "group_rename_succesfully"),
                            EMPTY_MAP,
                            createVariableMap(
                                    "group", lastName,
                                    "newName", values.get("newName").toString()
                            ),
                            EMPTY_MAP
                    )
            );
        } catch (SubjectIdentifierExistException e) {
            source.sendMessage(
                    parser.parse(
                            translator.getTranslation(getLanguage(source), "group_rename_error_name_exist"),
                            EMPTY_MAP,
                            createVariableMap(
                                    "newName", values.get("newName").toString()
                            ),
                            EMPTY_MAP
                    )
            );
        }
    }

    @CustomCommand(
            command = "pm create group #name",
            permission = PERMISSION_GROUP_CREATE,
            parsers = {}
    )
    public void createGroup(CommandSource source, Map<String, Object> values) {
        try {
            Group group = GroupCollection.instance.createGroup(values.get("name").toString());
            source.sendMessage(
                    parser.parse(
                            translator.getTranslation(getLanguage(source), "group_create_succesfully"),
                            EMPTY_MAP,
                            createVariableMap("group", values.get("name").toString()),
                            EMPTY_MAP
                    )
            );

            try {
                GroupCollection.instance.save(group.getIdentifier());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SubjectIdentifierExistException e) {
            source.sendMessage(
                    parser.parse(
                            translator.getTranslation(getLanguage(source), "group_create_error_name_exist"),
                            EMPTY_MAP,
                            createVariableMap("group", values.get("name").toString()),
                            EMPTY_MAP
                    )
            );
        }
    }

    @CustomCommand(
            command = "pm delete group #group",
            permission = PERMISSION_GROUP_DELETE,
            parsers = {@CustomParser(argument = "group", parser = GroupParser.class)}
    )
    public void deleteGroup(CommandSource source, Map<String, Object> values) {
        Group group = (Group) values.get("group");

        source.sendMessage(
                parser.parse(
                        translator.getTranslation(getLanguage(source), "group_delete_confirmation"),
                        createVariableMap("click_confirmation", translator.getTranslation(getLanguage(source), "click_confirmation")),
                        createVariableMap("group", group.getIdentifier()),
                        createVariableMap("click_confirmation", TextActions.executeCallback(source1 -> {
                            GroupCollection.instance.deleteGroup(group);

                            source.sendMessage(
                                    parser.parse(
                                            translator.getTranslation(getLanguage(source), "group_delete"),
                                            EMPTY_MAP,
                                            createVariableMap("group", group.getIdentifier()),
                                            EMPTY_MAP
                                    )
                            );

                        }))
                )
        );
    }

    @Override
    String getSubjectName(Subject subject) {
        return subject.getIdentifier();
    }
}
