package io.github.djxy.permissionmanager.rules.home.plugins;

import br.net.fabiozumbi12.redprotect.API.RedProtectAPI;
import br.net.fabiozumbi12.redprotect.Region;
import io.github.djxy.permissionmanager.rules.home.HomePlugin;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by samuelmarchildon-lavoie on 16-09-01.
 */
public class RedProtectPluginHome implements HomePlugin {

    @Override
    public boolean isPlayerInHisHome(Player player) {
        Region region = RedProtectAPI.getRegion(player.getLocation());

        return region != null && (region.isOwner(player) || region.isMember(player));
    }

}
