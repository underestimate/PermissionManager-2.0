package io.github.djxy.permissionmanager.rules.cooldown;

import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.rules.CommandRule;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-16.
 */
public class CooldownRule extends CommandRule {

    private final static Logger LOGGER = new Logger(CooldownRule.class);

    private final ConcurrentHashMap<UUID,Long> cooldowns = new ConcurrentHashMap<>();
    private long cooldown;

    public CooldownRule() {
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public boolean canApply(Player player) {
        return !super.isFromCommand(player) || !cooldowns.containsKey(player.getUniqueId()) || System.currentTimeMillis() >= cooldowns.get(player.getUniqueId());
    }

    @Override
    public void apply(Player player) {
        if(!isFromCommand(player))
            return;

        LOGGER.info(player.getName()+" is now cooldown for "+cooldown+" millisecondes.");
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis()+cooldown);
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        cooldown = node.getNode("time").getLong(0);
        super.deserialize(node);
    }

    @Override
    public void serialize(ConfigurationNode node) {
        node.getNode("time").setValue(cooldown);
        super.serialize(node);
    }

}
