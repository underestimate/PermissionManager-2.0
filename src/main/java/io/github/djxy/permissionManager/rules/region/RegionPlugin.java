package io.github.djxy.permissionManager.rules.region;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;

/**
 * Created by Samuel on 2016-08-16.
 */
public interface RegionPlugin {

    /**
     * Check if the player is in one of the regions of the list
     * @param player
     * @param regions
     * @return
     */
    public boolean isPlayerInRegion(Player player, Collection<String> regions);

}
