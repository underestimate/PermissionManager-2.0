package io.github.djxy.permissionmanager.rules.region.plugins;

import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.rules.region.RegionPlugin;
import net.foxdenstudio.sponge.foxguard.plugin.FGManager;
import net.foxdenstudio.sponge.foxguard.plugin.region.IRegion;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Samuel on 2016-08-19.
 */
public class FoxGuardPluginRegion implements RegionPlugin {

    private static final Logger LOGGER = new Logger(FoxGuardPluginRegion.class);

    @Override
    public boolean isPlayerInRegion(Player player, Collection<String> regions) {
        Set<IRegion> set = FGManager.getInstance().getRegionsAtPos(player.getWorld(), player.getLocation().getBlockPosition());

        for(IRegion iRegion : set)
            if (iRegion.contains(player.getLocation().getBlockPosition(), player.getWorld()) && regions.contains(iRegion.getName()))
                return true;

        return false;
    }

}
