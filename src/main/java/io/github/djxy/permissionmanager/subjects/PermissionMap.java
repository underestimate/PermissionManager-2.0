package io.github.djxy.permissionmanager.subjects;

import com.google.common.base.Preconditions;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-09.
 */
public class PermissionMap implements ConfigurationNodeSerializer, ConfigurationNodeDeserializer {

    private final ConcurrentHashMap<String,Permission> permissions;

    public PermissionMap() {
        this.permissions = new ConcurrentHashMap<>();
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
    }

    public void removePermission(String permission){
        Preconditions.checkNotNull(permission);

        permissions.remove(permission);
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
        permissions.clear();
    }

    public Map<String,Boolean> toMap(){
        Map<String,Boolean> map = new HashMap<>();

        for(Map.Entry pairs : permissions.entrySet())
            map.put((String) pairs.getKey(), ((Permission) pairs.getValue()).getValue());

        return map;
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

                permissions.put(perm, permission);
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
