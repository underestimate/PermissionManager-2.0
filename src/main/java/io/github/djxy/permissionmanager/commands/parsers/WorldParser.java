package io.github.djxy.permissionmanager.commands.parsers;

import io.github.djxy.customcommands.parsers.Parser;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-08-24.
 */
public class WorldParser extends Parser<World> {

    @Override
    public World parse(String value) {
        return Sponge.getServer().getWorld(value).isPresent()?Sponge.getServer().getWorld(value).get():null;
    }

    @Override
    public List<String> getSuggestions(String value) {
        ArrayList<String> worlds = new ArrayList<>();

        value = value.toLowerCase();

        for(World world : Sponge.getServer().getWorlds())
            if(world.getName().toLowerCase().startsWith(value))
                worlds.add(world.getName());

        if(worlds.size() == 1 && worlds.get(0).equalsIgnoreCase(value))
            worlds.clear();

        return worlds;
    }

}
