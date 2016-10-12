package io.github.djxy.permissionmanager.rules.home.plugins;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.Zone;
import io.github.djxy.permissionmanager.rules.home.HomePlugin;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Samuel on 2016-10-12.
 */
public class NationsPluginHome implements HomePlugin {

    @Override
    public boolean isPlayerInHisHome(Player player) {
        Nation nation = DataHandler.getNation(player.getLocation());

        if(nation != null) {
            Zone zone = nation.getZone(player.getLocation());

            return zone != null && (zone.isOwner(player.getUniqueId()) || zone.isCoowner(player.getUniqueId()));
        }
        else
            return false;
    }

}
