package io.github.djxy.permissionmanager.rules;

import com.google.common.base.Preconditions;
import io.github.djxy.customcommands.Util;
import io.github.djxy.permissionmanager.subjects.user.User;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Samuel on 2016-08-22.
 */
public abstract class CommandRule implements Rule{

    private final ConcurrentHashMap<String,CopyOnWriteArrayList<String[]>> commands = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<String> rawCommands = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<UUID,PlayerLastCommandCheck> players = new ConcurrentHashMap<>();

    public CommandRule() {
    }

    /**
     * @param command If the command in the chat is /help 1. The command here is help 1
     */
    public void addCommand(String command){
        Preconditions.checkNotNull(command);

        if(command.isEmpty())
            return;

        String args[] = Util.split(command);

        if(args.length == 0)
            return;

        if(!commands.containsKey(args[0].toLowerCase()))
            commands.put(args[0].toLowerCase(), new CopyOnWriteArrayList<>());

        rawCommands.add(command);
        commands.get(args[0].toLowerCase()).add(args);
    }

    public boolean isFromCommand(Player player) {
        if(!players.containsKey(player.getUniqueId()))
            players.put(player.getUniqueId(), new PlayerLastCommandCheck());

        PlayerLastCommandCheck playerLastCommandCheck = players.get(player.getUniqueId());
        
        if(Sponge.getServer().getRunningTimeTicks() == playerLastCommandCheck.tickLastCheck)
            return playerLastCommandCheck.isFromCommand;

        List<String> commandsOnTick = ((User) UserCollection.instance.get(player.getUniqueId().toString())).getCommandsOnCurrentTick();

        if(commandsOnTick.isEmpty()){
            playerLastCommandCheck.tickLastCheck = Sponge.getServer().getRunningTimeTicks();
            playerLastCommandCheck.isFromCommand = false;

            return false;
        }

        for(String command : commandsOnTick){
            String args[] = Util.split(command);

            if(args.length != 0 && commands.containsKey(args[0].toLowerCase())){
                CopyOnWriteArrayList<String[]> list = commands.get(args[0].toLowerCase());

                for(String commandToTest[] : list){
                    if(commandToTest.length == args.length){
                        boolean canApply = true;

                        for(int i = 1; i < commandToTest.length && canApply; i++){
                            if(!commandToTest[i].equals("*") && !commandToTest[i].equals(args[i]))
                                canApply = false;
                        }

                        if(canApply) {
                            playerLastCommandCheck.tickLastCheck = Sponge.getServer().getRunningTimeTicks();
                            playerLastCommandCheck.isFromCommand = true;

                            return true;
                        }
                    }
                }
            }
        }

        playerLastCommandCheck.tickLastCheck = Sponge.getServer().getRunningTimeTicks();
        playerLastCommandCheck.isFromCommand = false;

        return false;
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        List<ConfigurationNode> list = (List<ConfigurationNode>) node.getParent().getNode("commands").getChildrenList();

        for(ConfigurationNode value : list)
            addCommand(value.getString(""));
    }

    @Override
    public void serialize(ConfigurationNode node) {
        node.getParent().getNode("commands").setValue(rawCommands);
    }

    private class PlayerLastCommandCheck {

        private int tickLastCheck = -1;
        private boolean isFromCommand = false;

    }

}
