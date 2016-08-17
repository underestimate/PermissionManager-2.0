package io.github.djxy.permissionManager.rules.home;

import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Samuel on 2016-08-16.
 */
public interface HomePlugin {

    /**
     * Check if the player is in his home
     * @param player
     * @return
     */
    public boolean isPlayerInHisHome(Player player);

}
