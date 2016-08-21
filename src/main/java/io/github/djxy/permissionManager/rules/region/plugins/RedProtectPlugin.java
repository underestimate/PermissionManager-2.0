package io.github.djxy.permissionManager.rules.region.plugins;

import br.net.fabiozumbi12.redprotect.API.RedProtectAPI;
import br.net.fabiozumbi12.redprotect.Region;
import io.github.djxy.permissionManager.logger.Logger;
import io.github.djxy.permissionManager.rules.region.RegionPlugin;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;

/**
 * Created by Samuel on 2016-08-19.
 */
public class RedProtectPlugin implements RegionPlugin {

    private static final Logger LOGGER = new Logger(RedProtectPlugin.class);

    @Override
    public boolean isPlayerInRegion(Player player, Collection<String> regions) {
        Region region = RedProtectAPI.getRegion(player.getLocation());

        if(region != null)
            LOGGER.info(region.getName());

        return region != null && regions.contains(region.getName());
    }

}
