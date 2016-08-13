package io.github.djxy.permissionManager.subjects.group;

import com.google.common.base.Preconditions;
import io.github.djxy.permissionManager.subjects.ContextContainer;
import io.github.djxy.permissionManager.subjects.Permission;
import io.github.djxy.permissionManager.subjects.Subject;
import io.github.djxy.permissionManager.util.ContextUtil;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.util.Tristate;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Samuel on 2016-08-09.
 */
public class Group extends Subject implements Comparable<Group> {

    private int rank = Integer.MAX_VALUE;
    private final CopyOnWriteArraySet<GroupListener> listeners = new CopyOnWriteArraySet<>();

    protected Group(String identifier, GroupCollection collection) {
        super(identifier, collection);
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getOption(Set<Context> set, String s) {
        return getOption(set, s, new ArrayList<>());
    }

    @Override
    public Tristate getPermissionValue(Set<Context> set, String permission) {
        return getPermissionValue(set, permission, new ArrayList<>());
    }

    public void addListener(GroupListener listener){
        listeners.add(listener);
    }

    public void removeListener(GroupListener listener){
        listeners.remove(listener);
    }

    protected void delete(){
        for(GroupListener listener : listeners)
            listener.onGroupDeleted(this);
    }

    protected void setIdentifier(String identifier){
        this.identifier = identifier;
    }

    private Optional<String> getOption(Set<Context> set, String key, ArrayList<Group> groupsChecked){
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(groupsChecked);

        ContextContainer container = null;

        if(ContextUtil.isSingleContext(set))
            container = contexts.get(ContextUtil.getContext(set));

        if(container != null){
            String value = container.getOption(key);

            if (value != null)
                return Optional.of(value);
        }

        String value = globalContext.getOption(key);

        if(value != null)
            return Optional.of(value);

        groupsChecked.add(this);

        if(container != null) {
            for (Group group : container.getGroups()) {
                if (!groupsChecked.contains(group)) {
                    Optional<String> valueOpt = group.getOption(set, key);

                    if (valueOpt.isPresent())
                        return valueOpt;
                }
            }
        }

        for (Group group : globalContext.getGroups()) {
            if (!groupsChecked.contains(group)) {
                Optional<String> valueOpt = group.getOption(set, key);

                if (valueOpt.isPresent())
                    return valueOpt;
            }
        }

        return Optional.empty();
    }

    private Tristate getPermissionValue(Set<Context> set, String permission, ArrayList<Group> groupsChecked) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(permission);
        Preconditions.checkNotNull(groupsChecked);

        ContextContainer container = null;

        if(ContextUtil.isSingleContext(set))
            container = contexts.get(ContextUtil.getContext(set));

        if(container != null){
            Permission perm = container.getPermissions().getPermission(permission);

            if(perm != null)
                return Tristate.fromBoolean(perm.getValue());
        }

        Permission perm = globalContext.getPermissions().getPermission(permission);

        if(perm != null)
            return Tristate.fromBoolean(perm.getValue());

        groupsChecked.add(this);

        if(container != null) {
            for (Group group : container.getGroups()) {
                if (!groupsChecked.contains(group)) {
                    Tristate tristate = group.getPermissionValue(set, permission, groupsChecked);

                    if (!tristate.equals(Tristate.UNDEFINED))
                        return tristate;
                }
            }
        }

        for (Group group : globalContext.getGroups()) {
            if (!groupsChecked.contains(group)) {
                Tristate tristate = group.getPermissionValue(set, permission, groupsChecked);

                if (!tristate.equals(Tristate.UNDEFINED))
                    return tristate;
            }
        }

        return Tristate.UNDEFINED;
    }

    @Override
    public int compareTo(Group o) {
        if(this.rank > o.rank)
            return 1;
        if(this.rank < o.rank)
            return -1;
        else
            return 0;
    }
}
