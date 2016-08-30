package io.github.djxy.permissionmanager.commands.parsers;

import io.github.djxy.customcommands.parsers.Parser;
import io.github.djxy.permissionmanager.subjects.group.Group;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-08-24.
 */
public class GroupParser extends Parser<Group> {

    @Override
    public Group parse(String value) {
        return (Group) GroupCollection.instance.get(value);
    }

    @Override
    public List<String> getSuggestions(String value) {
        List<String> groups = new ArrayList<>();

        final String finalValue = value.toLowerCase();

        GroupCollection.instance.getAllSubjects().forEach(subject -> {
            if(subject.getIdentifier().toLowerCase().startsWith(finalValue))
                groups.add(subject.getIdentifier());
        });

        if(groups.size() == 1 && groups.get(0).equalsIgnoreCase(value))
            groups.clear();

        return groups;
    }
}
