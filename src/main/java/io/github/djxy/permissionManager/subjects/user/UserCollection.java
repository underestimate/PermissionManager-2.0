package io.github.djxy.permissionManager.subjects.user;

import com.google.common.base.Preconditions;
import io.github.djxy.permissionManager.exceptions.SubjectIdentifierExistException;
import io.github.djxy.permissionManager.subjects.SubjectCollection;
import org.spongepowered.api.service.permission.PermissionService;

import java.util.UUID;

/**
 * Created by Samuel on 2016-08-09.
 */
public class UserCollection extends SubjectCollection<User> {

    public UserCollection() {
        super(PermissionService.SUBJECTS_USER);
    }

    public synchronized User createUser(UUID uuid) throws SubjectIdentifierExistException {
        Preconditions.checkNotNull(uuid);

        if(userExist(uuid))
            throw new SubjectIdentifierExistException("User with the same UUID already exist.");

        User user = new User(uuid, this);

        subjects.put(uuid.toString(), user);

        user.addListener(this);

        return user;
    }

    @Override
    public User getDefaults() {
        return null;
    }

    private boolean userExist(UUID uuid){
        return hasRegistered(uuid.toString());
    }

}
