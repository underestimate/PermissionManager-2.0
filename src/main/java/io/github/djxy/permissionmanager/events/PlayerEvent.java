package io.github.djxy.permissionmanager.events;

import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

/**
 * Created by Samuel on 2016-08-17.
 */
public class PlayerEvent {

    @Listener
    public void onLoginIn(ClientConnectionEvent.Login event){
        try{
            UserCollection.instance.get(event.getProfile().getUniqueId().toString());
        } catch (Exception e){
            e.printStackTrace();
            event.setCancelled(true);
        }
    }

    @Listener
    public void onLogout(ClientConnectionEvent.Disconnect event){
        UserCollection.instance.unload(event.getTargetEntity().getUniqueId());
    }

    @Listener
    public void onCommandSend(SendCommandEvent event){
        CommandSource commandSource = event.getCause().first(CommandSource.class).get();

        if(!(commandSource instanceof Player))
            return;

        Player player = (Player) commandSource;

        User user = (User) UserCollection.instance.get(player.getUniqueId().toString());

        user.addCommandCurrentTick(event.getCommand() + " " + event.getArguments());
    }

}
