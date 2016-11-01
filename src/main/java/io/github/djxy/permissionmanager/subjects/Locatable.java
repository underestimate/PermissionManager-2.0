package io.github.djxy.permissionmanager.subjects;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by Samuel on 2016-11-01.
 */
public interface Locatable {

    public Location<World> getLocation();

    public World getWorld();

}
