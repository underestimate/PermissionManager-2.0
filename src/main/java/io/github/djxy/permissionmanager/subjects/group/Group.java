package io.github.djxy.permissionmanager.subjects.group;

import com.google.common.base.Preconditions;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.subjects.ContextContainer;
import io.github.djxy.permissionmanager.subjects.Permission;
import io.github.djxy.permissionmanager.subjects.Subject;
import io.github.djxy.permissionmanager.subjects.SubjectData;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.util.Tristate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Samuel on 2016-08-09.
 */
public class Group extends Subject implements Comparable<Group> {

    private final static Logger LOGGER = new Logger(Group.class);

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
    public Optional<String> getOption(Set<Context> set, String key) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(key);
        Optional<String> opt;

        if((opt = getOption((SubjectData) getSubjectData(), set, key)).isPresent()) {
            logGetOption(LOGGER, this, set, key, opt);
            return opt;
        }

        if((opt = getOption((SubjectData) getTransientSubjectData(), set, key)).isPresent()) {
            logGetOption(LOGGER, this, set, key, opt);
            return opt;
        }

        logGetOption(LOGGER, this, set, key, opt);

        return Optional.empty();
    }
    
    private Optional<String> getOption(SubjectData subjectData, Set<Context> set, String key){
        ArrayList<Group> groupsChecked = new ArrayList<>();

        if(subjectData.containsContexts(set)) {
            ContextContainer container = subjectData.getContextContainer(set);

            String value = container.getOption(key);

            if (value != null)
                return Optional.of(value);
        }

        if(subjectData.containsContexts(SubjectData.GLOBAL_CONTEXT)){
            ContextContainer container = subjectData.getContextContainer(SubjectData.GLOBAL_CONTEXT);

            String value = container.getOption(key);

            if (value != null)
                return Optional.of(value);
        }

        groupsChecked.add(this);

        if(subjectData.containsContexts(set)) {
            for (Group group : subjectData.getContextContainer(set).getGroups()) {
                if (!groupsChecked.contains(group)) {
                    Optional<String> valueOpt = group.getOption(set, key);

                    if (valueOpt.isPresent())
                        return valueOpt;
                }
            }
        }

        if(subjectData.containsContexts(SubjectData.GLOBAL_CONTEXT)){
            for (Group group : subjectData.getContextContainer(SubjectData.GLOBAL_CONTEXT).getGroups()) {
                if (!groupsChecked.contains(group)) {
                    Optional<String> valueOpt = group.getOption(set, key);

                    if (valueOpt.isPresent())
                        return valueOpt;
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Tristate getPermissionValue(Set<Context> set, String permission) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(permission);
        Permission perm;

        if((perm = getPermission(set, permission)) != null)
            return Tristate.fromBoolean(perm.getValue());

        return Tristate.UNDEFINED;
    }

    public Permission getPermission(Set<Context> set, String permission) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(permission);

        Permission perm = getPermission(set, permission, new ArrayList<>());

        if(perm != null)
            logGetPermissionValue(LOGGER, this, set, permission, Tristate.fromBoolean(perm.getValue()));
        else
            logGetPermissionValue(LOGGER, this, set, permission, Tristate.UNDEFINED);

        return perm;
    }

    private Permission getPermission(Set<Context> set, String permission, List<Group> groupsChecked) {
        Permission perm;

        if((perm = getPermission((SubjectData) getSubjectData(), set, permission, new ArrayList<>(groupsChecked))) != null)
            return perm;

        if((perm = getPermission((SubjectData) getTransientSubjectData(), set, permission, new ArrayList<>(groupsChecked))) != null)
            return perm;

        return null;
    }

    private Permission getPermission(SubjectData subjectData, Set<Context> set, String permission, List<Group> groupsChecked) {
        if(subjectData.containsContexts(set)) {
            Permission perm = subjectData.getContextContainer(set).getPermissions().getPermission(permission);
            
            if(perm != null)
                return perm;
        }

        if(subjectData.containsContexts(SubjectData.GLOBAL_CONTEXT)){
            Permission perm = subjectData.getContextContainer(SubjectData.GLOBAL_CONTEXT).getPermissions().getPermission(permission);

            if(perm != null)
                return perm;
        }

        groupsChecked.add(this);

        if(subjectData.containsContexts(set)) {
            for (Group group : subjectData.getContextContainer(set).getGroups()) {
                if (!groupsChecked.contains(group)) {
                    Permission perm = group.getPermission(set, permission, groupsChecked);

                    if (perm != null)
                        return perm;
                }
            }
        }

        if(subjectData.containsContexts(SubjectData.GLOBAL_CONTEXT)){
            for (Group group : subjectData.getContextContainer(SubjectData.GLOBAL_CONTEXT).getGroups()) {
                if (!groupsChecked.contains(group)) {
                    Permission perm = group.getPermission(set, permission, groupsChecked);

                    if (perm != null)
                        return perm;
                }
            }
        }

        return null;
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
