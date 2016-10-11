package io.github.djxy.permissionmanager.rules.nations;

import com.arckenver.nations.DataHandler;
import com.arckenver.nations.object.Nation;
import io.github.djxy.permissionmanager.rules.Rule;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel on 2016-10-11.
 */
public class NationsRule implements Rule {

    enum Location {OWN_NATION, OTHER_NATION, WILDERNESS}

    private final List<Location> locations = new ArrayList<>();

    @Override
    public boolean canApply(Player player) {
        if(!Sponge.getPluginManager().isLoaded("com.arckenver.nations"))
            return false;

        Nation nation = DataHandler.getNation(player.getLocation());
        boolean insideNation = nation != null;

        if(insideNation){
            boolean isMember = nation.isPresident(player.getUniqueId()) || nation.isMinister(player.getUniqueId()) || nation.isCitizen(player.getUniqueId());

            if(isMember)
                return locations.contains(Location.OWN_NATION);
            else
                return locations.contains(Location.OTHER_NATION);
        }
        else
            return locations.contains(Location.WILDERNESS);
    }

    @Override
    public void apply(Player player) {}

    @Override
    public void deserialize(ConfigurationNode node) {
        List<ConfigurationNode> values = (List<ConfigurationNode>) node.getNode("locations").getChildrenList();

        for(ConfigurationNode configurationNode : values){
            try{
                locations.add(Location.valueOf(configurationNode.getString().toUpperCase()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void serialize(ConfigurationNode node) {
        List<String> values = new ArrayList<>();

        for(Location location : locations)
            values.add(location.name());

        node.getNode("locations").setValue(values);
    }

}
