package io.github.djxy.permissionmanager.subjects.special;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.github.djxy.permissionmanager.subjects.*;
import io.github.djxy.permissionmanager.subjects.group.Group;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.*;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.util.Tristate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-10-13.
 */
public class Default implements org.spongepowered.api.service.permission.Subject {

    public static final String IDENTIFIER = "Default";
    public static final Default instance = new Default();

    private final SubjectData data = new io.github.djxy.permissionmanager.subjects.SubjectData(this);
    private final SubjectData transientData = new io.github.djxy.permissionmanager.subjects.SubjectData(this);

    private Default() {
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return Optional.empty();
    }

    @Override
    public SubjectCollection getContainingCollection() {
        return SpecialCollection.instance;
    }

    @Override
    public SubjectData getSubjectData() {
        return data;
    }

    @Override
    public SubjectData getTransientSubjectData() {
        return transientData;
    }

    @Override
    public boolean hasPermission(Set<Context> set, String s) {
        return getPermissionValue(set, s).asBoolean();
    }

    @Override
    public Tristate getPermissionValue(Set<Context> set, String s) {
        return Tristate.UNDEFINED;
    }

    @Override
    public boolean isChildOf(Set<Context> set, Subject subject) {
        return false;
    }

    @Override
    public List<Subject> getParents(Set<Context> set) {
        return new ArrayList<>();
    }

    @Override
    public Optional<String> getOption(Set<Context> set, String s) {
        return Optional.empty();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<Context> getActiveContexts() {
        return SubjectData.GLOBAL_CONTEXT;
    }

}
