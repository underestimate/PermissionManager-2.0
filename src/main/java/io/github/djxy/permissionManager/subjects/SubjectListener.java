package io.github.djxy.permissionManager.subjects;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;

import java.util.Set;

/**
 * Created by Samuel on 2016-08-13.
 */
public interface SubjectListener {

    public void onSetPermission(Set<Context> set, Subject subject, String permission, boolean value);

    public void onRemovePermission(Set<Context> set, Subject subject, String permission);

}
