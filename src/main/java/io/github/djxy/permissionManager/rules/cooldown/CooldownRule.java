package io.github.djxy.permissionManager.rules.cooldown;

import io.github.djxy.permissionManager.rules.Rule;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-16.
 */
public class CooldownRule implements Rule {

    private static final ConcurrentHashMap<UUID,Long> cooldowns = new ConcurrentHashMap<>();

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
        return !cooldowns.containsKey(player.getUniqueId()) || System.currentTimeMillis() >= cooldowns.get(player.getUniqueId());
    }

    @Override
    public void apply(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis()+cooldown);
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        cooldown = node.getLong(0);
    }

    @Override
    public void serialize(ConfigurationNode node) {
        node.setValue(cooldown);
    }

}
