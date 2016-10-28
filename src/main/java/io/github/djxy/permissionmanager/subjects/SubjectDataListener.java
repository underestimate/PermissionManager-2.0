package io.github.djxy.permissionmanager.subjects;

import io.github.djxy.permissionmanager.subjects.group.Group;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;

import java.util.Set;

/**
 * Created by Samuel on 2016-08-13.
 */
public interface SubjectDataListener {

    public void onSetPermission(Set<Context> set, Subject subject, Permission permission);

    public void onRemovePermission(Set<Context> set, Subject subject, String permission);

    default public void onAddGroup(Set<Context> set, Subject subject, Group group){}

    default public void onRemoveGroup(Set<Context> set, Subject subject, Group group){}

}
