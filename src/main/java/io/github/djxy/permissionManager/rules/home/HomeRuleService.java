package io.github.djxy.permissionmanager.rules.home;

import org.spongepowered.api.entity.living.player.Player;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Samuel on 2016-08-16.
 */
public class HomeRuleService {

    public static final HomeRuleService instance = new HomeRuleService();

    private final CopyOnWriteArrayList<HomePlugin> homePlugins = new CopyOnWriteArrayList<>();

    private HomeRuleService(){
    }

    public void addHomePlugin(HomePlugin homePlugin){
        homePlugins.add(homePlugin);
    }

    public void removeHomePlugin(HomePlugin homePlugin){
        homePlugins.remove(homePlugin);
    }

    /**
     * Check if the player is in his home
     * @param player
     * @return
     */
    public boolean isPlayerInHisHome(Player player){
        for(HomePlugin homePlugin : homePlugins)
            if(homePlugin.isPlayerInHisHome(player))
                return true;

        return false;
    }

}
