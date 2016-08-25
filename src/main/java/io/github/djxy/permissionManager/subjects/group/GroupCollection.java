package io.github.djxy.permissionmanager.subjects.group;

import com.google.common.base.Preconditions;
import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierExistException;
import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierInvalidException;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.subjects.SubjectCollection;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Samuel on 2016-08-09.
 */
public class GroupCollection extends SubjectCollection {

    private static final Logger LOGGER = new Logger(GroupCollection.class);

    public final static GroupCollection instance = new GroupCollection();

    private Group defaultGroup;
    private final Listener groupListener = new Listener();

    private GroupCollection() {
        super(PermissionService.SUBJECTS_GROUP, "Group");
    }

    @Override
    public Subject getDefaults() {
        return defaultGroup;
    }

    public synchronized void renameGroup(Group group, String identifier) throws SubjectIdentifierExistException {
        if(hasRegistered(identifier))
            throw new SubjectIdentifierExistException("There is already a group named "+identifier+".");

        subjects.put(identifier, group);
        subjects.remove(group.getIdentifier());

        LOGGER.info("Group " + group.getIdentifier() + " renamed " + identifier+".");

        File file = directory.resolve(group.getIdentifier()+".yml").toFile();

        if(file.exists()) {
            file.delete();
            LOGGER.info(group.getIdentifier() + ".yml has been deleted.");
        }

        group.setIdentifier(identifier);

        try {
            save(identifier);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteGroup(Group group){
        subjects.remove(group.getIdentifier());

        group.delete();

        LOGGER.info("Group " + group.getIdentifier() + " deleted.");

        File file = directory.resolve(group.getIdentifier()+".yml").toFile();

        if(!file.exists())
            return;

        file.delete();
    }

    public synchronized Group createGroup(String identifier) throws SubjectIdentifierExistException {
        Preconditions.checkNotNull(identifier);

        if(hasRegistered(identifier))
            throw new SubjectIdentifierExistException("There is already a group named "+identifier+".");

        Group group = new Group(identifier, this);

        subjects.put(identifier, group);

        group.addListener(subjectListener);
        group.addListener(groupListener);

        LOGGER.info("Group " + group.getIdentifier() + " created.");

        return group;
    }

    public void createDefaultGroup(){
        if(GroupCollection.instance.getDefaults() != null)
            return;

        try {
            Group group;

            if(GroupCollection.instance.hasRegistered("default"))
                group = GroupCollection.instance.createGroup(UUID.randomUUID().toString());
            else
                group = GroupCollection.instance.createGroup("default");

            group.setDefaultGroup(true);

            LOGGER.info("Default group(" + group.getIdentifier() + ") created.");
        } catch (SubjectIdentifierExistException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Subject createSubjectFromFile(String identifier) throws SubjectIdentifierInvalidException, SubjectIdentifierExistException {
        Preconditions.checkNotNull(identifier);

        return createGroup(identifier);
    }

    private class Listener implements GroupListener{

        @Override
        public void onGroupDeleted(Group group) {}

        @Override
        public void onGroupRankChange() {}

        @Override
        public void onGroupSetDefault(Group group) {
            if(defaultGroup != null)
                defaultGroup.setDefaultGroup(false);

            if(group == defaultGroup)
                return;

            defaultGroup = group;
            LOGGER.info("New default group("+defaultGroup.getIdentifier()+").");
        }

    }
}
