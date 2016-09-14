package io.github.djxy.permissionmanager.enjin;

import com.enjin.sponge.permissions.PermissionHandler;
import com.google.common.collect.Sets;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by samuelmarchildon-lavoie on 16-09-14.
 */
public class PermissionManagerHandler implements PermissionHandler {

    @Override
    public void onJoin(ClientConnectionEvent.Join join) {}

    @Override
    public void onDisconnect(ClientConnectionEvent.Disconnect disconnect) {}

    @Override
    public Map<String, List<String>> fetchPlayerGroups(GameProfile gameProfile) {
        UserCollection collection = UserCollection.instance;

        Subject subject = collection.get(gameProfile.getUniqueId().toString());

        Map<String, List<String>> worlds = new HashMap<>();

        for(World world : Sponge.getServer().getWorlds()){
            List<Subject> parents = subject.getParents(Sets.newHashSet(new Context(Context.WORLD_KEY, world.getName())));

            if(!parents.isEmpty())
                worlds.put(world.getName(), parents.stream().map(Subject::getIdentifier).collect(Collectors.toList()));
        }

        return worlds;
    }

    @Override
    public List<String> fetchGroups() {
        List<String> groups = new ArrayList<>();

        for(Subject group : GroupCollection.instance.getAllSubjects())
            groups.add(group.getIdentifier());

        return groups;
    }

    @Override
    public void addGroup(String player, String group, String world) {

    }

    @Override
    public void removeGroup(String player, String group, String world) {

    }

}
