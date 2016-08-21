package io.github.djxy.permissionManager.events;

import io.github.djxy.permissionManager.exceptions.SubjectIdentifierExistException;
import io.github.djxy.permissionManager.subjects.user.UserCollection;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

/**
 * Created by Samuel on 2016-08-17.
 */
public class PlayerLoginEvent {

    @Listener
    public void onLoginIn(ClientConnectionEvent.Login event){
        String identifier = event.getProfile().getUniqueId().toString();

        if(!UserCollection.instance.canLoadSubject(identifier)) {
            try {
                UserCollection.instance.createUser(event.getProfile().getUniqueId());
            } catch (SubjectIdentifierExistException e) {
                e.printStackTrace();
                event.setCancelled(true);
            }
            return;
        }

        if(!UserCollection.instance.load(identifier))
            event.setCancelled(true);
    }

    @Listener
    public void onLogout(ClientConnectionEvent.Disconnect event){
        UserCollection.instance.unload(event.getTargetEntity().getUniqueId());
    }

}
