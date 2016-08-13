package io.github.djxy.permissionManager.subjects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.github.djxy.permissionManager.subjects.group.Group;
import io.github.djxy.permissionManager.subjects.group.GroupListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Samuel on 2016-08-09.
 */
public class ContextContainer implements GroupListener {

    private final PermissionMap permissions;
    private final CopyOnWriteArrayList<Group> groups;
    private final ConcurrentHashMap<String, String> options;

    public ContextContainer() {
        this.permissions = new PermissionMap();
        this.groups = new CopyOnWriteArrayList<>();
        this.options = new ConcurrentHashMap<>();
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

        Collections.sort(groups);
    }

    public void removeGroup(Group group){
        Preconditions.checkNotNull(group);

        group.removeListener(this);

        groups.remove(group);
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

}
