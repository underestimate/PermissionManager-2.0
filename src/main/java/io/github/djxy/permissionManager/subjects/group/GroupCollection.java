package io.github.djxy.permissionManager.subjects.group;

import io.github.djxy.permissionManager.exceptions.SubjectIdentifierExistException;
import io.github.djxy.permissionManager.subjects.SubjectCollection;
import org.spongepowered.api.service.permission.PermissionService;

/**
 * Created by Samuel on 2016-08-09.
 */
public class GroupCollection extends SubjectCollection<Group> {

    public final static GroupCollection instance = new GroupCollection();

    private GroupCollection() {
        super(PermissionService.SUBJECTS_GROUP);
    }

    @Override
    public Group getDefaults() {
        return null;
    }

    public synchronized void renameGroup(Group group, String identifier) throws SubjectIdentifierExistException {
        if(hasRegistered(identifier))
            throw new SubjectIdentifierExistException("There is already a group named "+identifier+".");

        subjects.put(identifier, group);
        subjects.remove(group.getIdentifier());

        group.setIdentifier(identifier);
    }

    public void deleteGroup(Group group){
        subjects.remove(group.getIdentifier());

        group.delete();
    }

    public synchronized Group createGroup(String identifier) throws SubjectIdentifierExistException {
        if(hasRegistered(identifier))
            throw new SubjectIdentifierExistException("There is already a group named "+identifier+".");

        Group group = new Group(identifier, this);

        subjects.put(identifier, group);

        group.addListener(this);

        return group;
    }

}
