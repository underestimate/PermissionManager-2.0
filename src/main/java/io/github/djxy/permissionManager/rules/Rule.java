package io.github.djxy.permissionmanager.rules;

import io.github.djxy.permissionmanager.subjects.ConfigurationNodeDeserializer;
import io.github.djxy.permissionmanager.subjects.ConfigurationNodeSerializer;
import org.spongepowered.api.entity.living.player.Player;

/**
 * Created by Samuel on 2016-08-16.
 */
public interface Rule extends ConfigurationNodeDeserializer, ConfigurationNodeSerializer {

    /**
     * Check if can apply the rule to the player
     * @param player
     * @return
     */
    public boolean canApply(Player player);

    /**
     * If you need to do an action with the rule, you should do it inside this method. Example: Remove money, start a cooldown
     * @param player
     */
    public void apply(Player player);

}
