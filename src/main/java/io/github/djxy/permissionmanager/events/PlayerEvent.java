package io.github.djxy.permissionmanager.events;

import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierExistException;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;

/**
 * Created by Samuel on 2016-08-17.
 */
public class PlayerEvent {

    @Listener
    public void onCommandSend(SendCommandEvent event){
        CommandSource commandSource = event.getCause().first(CommandSource.class).get();

        if(!(commandSource instanceof Player))
            return;

        Player player = (Player) commandSource;

        User user = (User) UserCollection.instance.get(player.getUniqueId().toString());

        user.addCommandCurrentTick(event.getCommand() + " " + event.getArguments());
    }

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
    public void test(ClientConnectionEvent.Join event){
        PaginationService service = Sponge.getServiceManager().provide(PaginationService.class).get();

        service.builder()
                .padding(Text.of("-"))
                .title(Text.of("Title"))
                .contents(
                        Text.of("1"),
                        Text.of("2"),
                        Text.of("3"),
                        Text.of("4"),
                        Text.of("5"),
                        Text.of("6"),
                        Text.of("7"),
                        Text.of("8"),
                        Text.of("9"),
                        Text.of("0"),
                        Text.of("12"),
                        Text.of("1"),
                        Text.of("3"),
                        Text.of("5"),
                        Text.of("76"),
                        Text.of("89"),
                        Text.of("-"),
                        Text.of("f"),
                        Text.of("d"),
                        Text.of("x")
                ).sendTo(event.getTargetEntity());
    }

    @Listener
    public void onLogout(ClientConnectionEvent.Disconnect event){
        UserCollection.instance.unload(event.getTargetEntity().getUniqueId());
    }

}
