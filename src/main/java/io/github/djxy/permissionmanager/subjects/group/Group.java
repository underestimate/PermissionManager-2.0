package io.github.djxy.permissionmanager.subjects.group;

import com.google.common.base.Preconditions;
import io.github.djxy.permissionmanager.subjects.ContextContainer;
import io.github.djxy.permissionmanager.subjects.Permission;
import io.github.djxy.permissionmanager.subjects.Subject;
import io.github.djxy.permissionmanager.util.ContextUtil;
import ninja.leaping.configurate.ConfigurationNode;
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
    private boolean defaultGroup = false;
    private final CopyOnWriteArraySet<GroupListener> listeners = new CopyOnWriteArraySet<>();

    protected Group(String identifier, GroupCollection collection) {
        super(identifier, collection);
    }

    public void setRank(int rank) {
        this.rank = rank;

        for(GroupListener listener : listeners)
            listener.onGroupRankChange();
    }

    public int getRank() {
        return rank;
    }

    public boolean isDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(boolean defaultGroup) {
        this.defaultGroup = defaultGroup;

        if(this.defaultGroup)
            for(GroupListener listener : listeners)
                listener.onGroupSetDefault(this);
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
        Permission permission1 = getPermissionValue(set, permission, new ArrayList<>());

        return permission1 == null?Tristate.UNDEFINED:Tristate.fromBoolean(permission1.getValue());
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

    public Permission getPermissionValue(Set<Context> set, String permission, ArrayList<Group> groupsChecked) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(permission);
        Preconditions.checkNotNull(groupsChecked);

        ContextContainer container = null;

        if(ContextUtil.isSingleContext(set))
            container = contexts.get(ContextUtil.getContext(set));

        if(container != null){
            Permission perm = container.getPermissions().getPermission(permission);

            if(perm != null)
                return perm;
        }

        Permission perm = globalContext.getPermissions().getPermission(permission);

        if(perm != null)
            return perm;

        groupsChecked.add(this);

        if(container != null) {
            for (Group group : container.getGroups()) {
                if (!groupsChecked.contains(group)) {
                    perm = group.getPermissionValue(set, permission, groupsChecked);

                    if (perm != null)
                        return perm;
                }
            }
        }

        for (Group group : globalContext.getGroups()) {
            if (!groupsChecked.contains(group)) {
                perm = group.getPermissionValue(set, permission, groupsChecked);

                if (perm != null)
                    return perm;
            }
        }

        return null;
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);
        rank = node.getNode("rank").getInt(Integer.MAX_VALUE);
        setDefaultGroup(node.getNode("default").getBoolean(false));
    }

    @Override
    public void serialize(ConfigurationNode node) {
        super.serialize(node);

        if(rank != Integer.MAX_VALUE)
            node.getNode("rank").setValue(rank);

        if(defaultGroup)
            node.getNode("default").setValue(defaultGroup);
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
