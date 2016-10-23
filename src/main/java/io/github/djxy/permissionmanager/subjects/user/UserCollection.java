package io.github.djxy.permissionmanager.subjects.user;

import com.google.common.base.Preconditions;
import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierExistException;
import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierInvalidException;
import io.github.djxy.permissionmanager.exceptions.UserCreationException;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.subjects.SubjectCollection;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectData;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Samuel on 2016-08-09.
 */
public class UserCollection extends SubjectCollection {

    private static final Logger LOGGER = new Logger(UserCollection.class);

    public final static UserCollection instance = new UserCollection();

    private UserCollection() {
        super(PermissionService.SUBJECTS_USER, "User");
    }

    public User get(Player player){
        Preconditions.checkNotNull(player);

        return (User) subjects.get(player.getUniqueId().toString());
    }

    public synchronized User createUser(UUID uuid) throws SubjectIdentifierExistException {
        return createUser(uuid, false);
    }

    @Override
    public Subject get(String identifier) {
        Preconditions.checkNotNull(identifier);
        UUID uuid = UUID.fromString(identifier);

        if(subjects.containsKey(identifier))
            return subjects.get(identifier);

        if(!canLoadSubject(identifier)) {
            try {
                return createUser(uuid);
            } catch (SubjectIdentifierExistException e) {
                e.printStackTrace();
            }
        }

        if(!UserCollection.instance.load(identifier))
            throw new UserCreationException("Can't create user "+identifier+".");

        return subjects.get(identifier);
    }

    public void unload(UUID uuid){
        Preconditions.checkNotNull(uuid);

        if(!hasRegistered(uuid.toString()))
            return;

        try {
            save(uuid.toString());

            subjects.remove(uuid.toString());

            LOGGER.info("User: "+uuid+" - Unloaded.");
            LOGGER.info(subjects.size()+" user(s) loaded.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Subject createSubjectFromFile(String identifier) throws SubjectIdentifierInvalidException, SubjectIdentifierExistException {
        Preconditions.checkNotNull(identifier);
        UUID uuid = null;

        try{
            uuid = UUID.fromString(identifier);
        }catch (Exception e){
            throw new SubjectIdentifierInvalidException(identifier+" is invalid.");
        }

        return createUser(uuid, true);
    }

    private synchronized User createUser(UUID uuid, boolean fromFile) throws SubjectIdentifierExistException {
        Preconditions.checkNotNull(uuid);

        if((fromFile && hasRegistered(getIdentifier())) || (!fromFile && userExist(uuid)))
            throw new SubjectIdentifierExistException("User with the same UUID already exist.");

        User user = new User(uuid, this);

        subjects.put(uuid.toString(), user);

        if(!fromFile)
            user.getSubjectData().addParent(SubjectData.GLOBAL_CONTEXT, GroupCollection.instance.getDefaults());

        user.addListener(subjectListener);

        LOGGER.info("User: "+uuid+" - Created from file "+fromFile);

        return user;
    }

    @Override
    public synchronized boolean load(String identifier) {
        boolean loaded = super.load(identifier);

        if(loaded)
            LOGGER.info(subjects.size()+" user(s) loaded.");

        return loaded;
    }

    private boolean userExist(UUID uuid){
        return hasRegistered(uuid.toString()) || directory.resolve(uuid.toString()+".yml").toFile().exists();
    }

}
