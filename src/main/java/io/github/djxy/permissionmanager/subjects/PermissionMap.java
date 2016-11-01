package io.github.djxy.permissionmanager.subjects;

import com.google.common.base.Preconditions;
import io.github.djxy.permissionmanager.util.ImmutableMap;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-09.
 */
public class PermissionMap implements ConfigurationNodeSerializer, ConfigurationNodeDeserializer {

    private final ConcurrentHashMap<String,Permission> permissions;
    private final Set<SubjectDataListener> listeners;
    private final Set<Context> contexts;
    private final org.spongepowered.api.service.permission.Subject subject;
    private final ConcurrentHashMap<String,Boolean> map = new ConcurrentHashMap<>();
    private final ImmutableMap<String,Boolean> immutableMap;

    public PermissionMap(Set<SubjectDataListener> listeners, Set<Context> contexts, Subject subject) {
        this.listeners = listeners;
        this.contexts = contexts;
        this.subject = subject;
        this.permissions = new ConcurrentHashMap<>();
        this.immutableMap = new ImmutableMap<String,Boolean>(map){
            @Override
            public void clear() {}
        };

    }

    public List<Permission> getPermissions(){
        return new ArrayList<>(permissions.values());
    }

    public boolean isEmpty(){
        return permissions.isEmpty();
    }

    public void putPermission(String permission, Permission perm){
        Preconditions.checkNotNull(perm);
        Preconditions.checkNotNull(permission);

        permissions.put(permission, perm);
        map.put(permission, perm.getValue());

        if(subject != null)
            for(SubjectDataListener listener : listeners)
                listener.onSetPermission(contexts, subject, perm);
    }

    public void removePermission(String permission){
        Preconditions.checkNotNull(permission);

        permissions.remove(permission);
        map.remove(permission);

        if(subject != null)
            for(SubjectDataListener listener : listeners)
                listener.onRemovePermission(contexts, subject, permission);
    }

    public Permission getPermission(String permission) {
        Preconditions.checkNotNull(permission);

        if(permissions.containsKey(permission))
            return permissions.get(permission);
        if(!permission.contains("."))
            return null;

        int count = permission.length() - permission.replace(".", "").length();
        String tmp = permission;
        Permission perm;

        for(int i = 0; i < count; i++){
            tmp = permission.substring(0, tmp.lastIndexOf("."));

            if((perm = this.permissions.get(tmp)) != null)
                return perm;

            if((perm = this.permissions.get(tmp+".*")) != null)
                return perm;
        }

        return this.permissions.get("*");
    }

    public void clear(){
        if(subject != null)
            for(String permission : permissions.keySet())
                for(SubjectDataListener listener : listeners)
                    listener.onRemovePermission(contexts, subject, permission);

        permissions.clear();
        map.clear();
    }

    public Map<String,Boolean> toMap(){
        return immutableMap;
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        List<ConfigurationNode> permissionList = (List<ConfigurationNode>) node.getNode("permissions").getChildrenList();

        for(ConfigurationNode nodeValue : permissionList){
            String value = nodeValue.getString("");

            if(!value.isEmpty()){
                boolean permValue = !value.startsWith("-");
                String perm = !permValue ?value.substring(1):value;
                Permission permission = new Permission(perm, permValue);

                putPermission(perm, permission);
            }
        }
    }

    @Override
    public void serialize(ConfigurationNode node) {
        List<String> permissions = new ArrayList<>();

        for(Map.Entry pairs : this.permissions.entrySet()){
            String permission = (String) pairs.getKey();
            Permission perm = (Permission) pairs.getValue();

            permissions.add((!perm.getValue() ?"-":"")+permission);
        }

        node.getNode("permissions").setValue(permissions);
    }
}
