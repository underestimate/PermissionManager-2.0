package io.github.djxy.permissionmanager.rules.region;

import com.google.common.collect.ImmutableSet;
import io.github.djxy.permissionmanager.rules.Rule;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Samuel on 2016-08-16.
 */
public class RegionRule implements Rule {

    private final CopyOnWriteArraySet<String> regions = new CopyOnWriteArraySet<>();
    private boolean isBlackList = false;

    public void addRegion(String region){
        regions.add(region);
    }

    public void removeRegion(String region){
        regions.remove(region);
    }

    @Override
    public boolean canApply(Player player) {
        return RegionRuleService.instance.isPlayerInRegion(player, ImmutableSet.copyOf(regions)) == !isBlackList;
    }

    @Override
    public void apply(Player player) {
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        if(node.hasListChildren()) {
            List<ConfigurationNode> regionlist = (List<ConfigurationNode>) node.getChildrenList();

            for(ConfigurationNode nodeValue : regionlist){
                String value = nodeValue.getString("");

                if(!value.isEmpty())
                    regions.add(value);
            }
        }
        else {
            List<ConfigurationNode> regionlist = (List<ConfigurationNode>) node.getNode("list").getChildrenList();

            for(ConfigurationNode nodeValue : regionlist){
                String value = nodeValue.getString("");

                if(!value.isEmpty())
                    regions.add(value);
            }

            isBlackList = node.getNode("blacklist").getBoolean(false);
        }
    }

    @Override
    public void serialize(ConfigurationNode node) {
        List<String> regions = new ArrayList<>();

        for(String region : this.regions)
            regions.add(region);

        node.getNode("list").setValue(regions);

        if(isBlackList)
            node.getNode("blacklist").setValue(isBlackList);
    }

}
