package io.github.djxy.permissionmanager.rules.home;

import io.github.djxy.permissionmanager.rules.Rule;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Samuel on 2016-08-16.
 */
public class HomeRule implements Rule {

    public HomeRule() {
    }

    @Override
    public boolean canApply(Player player) {
        return HomeRuleService.instance.isPlayerInHisHome(player);
    }

    @Override
    public void apply(Player player) {
    }

    @Override
    public void deserialize(ConfigurationNode node) {
    }

    @Override
    public void serialize(ConfigurationNode node) {
        node.setValue(true);
    }

}
