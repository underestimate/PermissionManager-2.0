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

        String permissions[] = new String[permission.length() - permission.replace(".", "").length() + 1];
        int lastIndex = 1;

        permissions[0] = "*";

        for(int i = 0; i < permission.length(); i++){
            if(permission.charAt(i) == '.')
                permissions[lastIndex++] = permission.substring(0, i) + ".*";
        }

        for(int i = permissions.length-1; i >= 0; i--){
            Permission perm = this.permissions.get(permissions[i]);

            if(perm != null)
                return perm;
        }

        return null;
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
