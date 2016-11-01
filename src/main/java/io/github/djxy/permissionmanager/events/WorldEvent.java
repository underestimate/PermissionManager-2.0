package io.github.djxy.permissionmanager.events;

import io.github.djxy.permissionmanager.promotion.Promotions;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;
import io.github.djxy.permissionmanager.subjects.special.Default;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.SaveWorldEvent;

/**
 * Created by samuelmarchildon-lavoie on 16-10-26.
 */
public class WorldEvent {

    @Listener
    public void onSaveWorld(SaveWorldEvent.Post event){
        Default.instance.save();
        UserCollection.instance.save();
        GroupCollection.instance.save();
        Promotions.instance.save();
    }

}
