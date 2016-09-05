package io.github.djxy.permissionmanager.subjects.special;

import io.github.djxy.permissionmanager.subjects.Subject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;
import java.util.Set;

/**
 * Created by Samuel on 2016-08-17.
 */
public class Special extends Subject {

    public Special(String identifier, SubjectCollection collection) {
        super(identifier, collection);
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return Optional.of(Sponge.getServer().getConsole());
    }

    @Override
    public Tristate getPermissionValue(Set<Context> set, String s) {
        return Tristate.TRUE;
    }

    public Optional<String> getOption(Set<Context> set, String s) {
        return Optional.empty();
    }

}
