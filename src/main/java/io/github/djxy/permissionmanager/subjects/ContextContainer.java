package io.github.djxy.permissionmanager.subjects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.github.djxy.permissionmanager.subjects.group.Group;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;
import io.github.djxy.permissionmanager.subjects.group.GroupListener;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Samuel on 2016-08-09.
 */
public class ContextContainer implements GroupListener, ConfigurationNodeSerializer, ConfigurationNodeDeserializer {

    private final PermissionMap permissions;
    private final CopyOnWriteArrayList<Group> groups;
    private final ConcurrentHashMap<String, String> options;
    private final Subject subject;
    private final Set<Context> contexts;
    private final Set<SubjectDataListener> listeners;

    public ContextContainer(Subject subject, Set<Context> contexts, Set<SubjectDataListener> listeners) {
        this.subject = subject;
        this.permissions = new PermissionMap(listeners, contexts, subject);
        this.groups = new CopyOnWriteArrayList<>();
        this.options = new ConcurrentHashMap<>();
        this.listeners = listeners;
        this.contexts = contexts;
    }

    public ContextContainer() {
        this(null, new HashSet<>(), new HashSet<>());
    }

    public boolean isEmpty(){
        return permissions.isEmpty() && groups.isEmpty() && options.isEmpty();
    }

    public PermissionMap getPermissions() {
        return permissions;
    }

    public List<Group> getGroups() {
        return ImmutableList.copyOf(groups);
    }

    public boolean hasGroup(Group group){
        Preconditions.checkNotNull(group);

        return groups.contains(group);
    }

    public void addGroup(Group group){
        Preconditions.checkNotNull(group);

        if(groups.contains(group))
            return;

        group.addListener(this);

        groups.add(group);

        if(subject != null)
            for(SubjectDataListener listener : listeners)
                listener.onAddGroup(contexts, subject, group);

        Collections.sort(groups);
    }

    public void removeGroup(Group group){
        Preconditions.checkNotNull(group);

        group.removeListener(this);

        groups.remove(group);

        if(subject != null)
            for(SubjectDataListener listener : listeners)
                listener.onRemoveGroup(contexts, subject, group);
    }

    public Map<String,String> getOptions(){
        return new HashMap<>(options);
    }

    public void setOption(String key, String value){
        options.put(key, value);
    }

    public String getOption(String key){
        return options.get(key);
    }

    public void removeOption(String key){
        options.remove(key);
    }

    public void clearOptions(){
        options.clear();
    }

    public void clearGroups(){
        groups.clear();
    }

    @Override
    public void onGroupDeleted(Group group) {
        removeGroup(group);
    }

    @Override
    public void onGroupRankChange() {
        Collections.sort(groups);
    }

    @Override
    public void onGroupSetDefault(Group group) {}

    @Override
    public void deserialize(ConfigurationNode node) {
        permissions.deserialize(node);

        List<ConfigurationNode> groupList = (List<ConfigurationNode>) node.getNode("groups").getChildrenList();

        for(ConfigurationNode nodeValue : groupList){
            String value = nodeValue.getString("");

            if(!value.isEmpty() && GroupCollection.instance.hasRegistered(value))
                addGroup((Group) GroupCollection.instance.get(value));
        }

        Map<Object,ConfigurationNode> dataMap = (Map<Object, ConfigurationNode>) node.getNode("options").getChildrenMap();

        for(Object data : dataMap.keySet())
            setOption(data.toString(), dataMap.get(data).getString(""));


        for(Permission permission : permissions.getPermissions())
            if(!node.getNode("rules", permission.getPermission()).isVirtual())
                permission.deserialize(node.getNode("rules", permission.getPermission()));
    }

    @Override
    public void serialize(ConfigurationNode node) {
        if(!permissions.isEmpty())
            permissions.serialize(node);

        if(!groups.isEmpty()) {
            List<String> groups = new ArrayList<>();

            for (Group group : this.groups)
                groups.add(group.getIdentifier());

            node.getNode("groups").setValue(groups);
        }

        if(!options.isEmpty()) {
            for (Map.Entry pairs : options.entrySet())
                node.getNode("options", pairs.getKey()).setValue(pairs.getValue());
        }

        if(!permissions.isEmpty()) {
            for (Permission permission : permissions.getPermissions())
                if (!permission.getRules().isEmpty())
                    permission.serialize(node.getNode("rules", permission.getPermission()));
        }
    }
}
