package io.github.djxy.permissionManager.rules.region;

import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Samuel on 2016-08-16.
 */
public class RegionRuleService {

    public static final RegionRuleService instance = new RegionRuleService();

    private final CopyOnWriteArrayList<RegionPlugin> regionPlugins = new CopyOnWriteArrayList<>();

    private RegionRuleService(){
    }

    public void addRegionPlugin(RegionPlugin regionPlugin){
        regionPlugins.add(regionPlugin);
    }

    public void removeRegionPlugin(RegionPlugin regionPlugin){
        regionPlugins.remove(regionPlugin);
    }

    /**
     * Check if the player is in one of the regions of the list
     * @param player
     * @param player
     * @return
     */
    public boolean isPlayerInRegion(Player player, Collection<String> regions){
        for(RegionPlugin regionPlugin : regionPlugins)
            if(regionPlugin.isPlayerInRegion(player, regions))
                return true;

        return false;
    }

}
