package io.github.djxy.permissionManager.subjects;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-09.
 */
public class PermissionMap {

    private final ConcurrentHashMap<String,Permission> permissions;

    public PermissionMap() {
        this.permissions = new ConcurrentHashMap<>();
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

}
