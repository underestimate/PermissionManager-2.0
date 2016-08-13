package io.github.djxy.permissionManager.subjects;

import com.google.common.base.Preconditions;

/**
 * Created by Samuel on 2016-08-09.
 */
public class Permission {

    private final String permission;
    private boolean value;

    public Permission(String permission, boolean value) {
        Preconditions.checkNotNull(permission);

        this.permission = permission;
        this.value = value;
    }

    public String getPermission() {
        return permission;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

}
