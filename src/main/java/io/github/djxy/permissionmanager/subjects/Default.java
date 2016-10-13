package io.github.djxy.permissionmanager.subjects;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Samuel on 2016-10-13.
 */
public class Default extends Subject {

    public Default(org.spongepowered.api.service.permission.SubjectCollection collection) {
        super("Default", collection);
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return Optional.empty();
    }

    @Override
    public Tristate getPermissionValue(Set<Context> set, String s) {
        if(contexts.containsKey(set)){
            Permission permission = contexts.get(set).getPermissions().getPermission(s);

            if(permission == null)
                return Tristate.UNDEFINED;

            return Tristate.fromBoolean(permission.getValue());
        }

        return Tristate.UNDEFINED;
    }

    @Override
    public Optional<String> getOption(Set<Context> set, String s) {
        if(contexts.containsKey(set)){
            String option = contexts.get(set).getOption(s);

            if(option == null)
                return Optional.empty();

            return Optional.of(option);
        }

        return Optional.empty();
    }

}
