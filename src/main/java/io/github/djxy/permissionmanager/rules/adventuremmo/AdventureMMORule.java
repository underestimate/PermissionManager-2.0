package io.github.djxy.permissionmanager.rules.adventuremmo;

import io.github.djxy.permissionmanager.rules.Rule;
import me.mrdaniel.mmo.enums.SkillType;
import me.mrdaniel.mmo.io.players.MMOPlayer;
import me.mrdaniel.mmo.io.players.MMOPlayerDatabase;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Samuel on 2016-10-11.
 */
public class AdventureMMORule implements Rule {

    private final Map<String,Integer> skills = new HashMap<>();

    @Override
    public boolean canApply(Player player) {
        if(!Sponge.getPluginManager().isLoaded("adventuremmo"))
            return false;

        MMOPlayer mmoPlayer = MMOPlayerDatabase.getInstance().getOrCreatePlayer(player.getUniqueId());

        for(String skill : skills.keySet()){
            try{
                SkillType type = SkillType.valueOf(skill);

                if(mmoPlayer.getSkills().getSkill(type).level < skills.get(skill))
                    return false;
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    @Override
    public void apply(Player player) {}

    @Override
    public void deserialize(ConfigurationNode node) {
        Map<Object,ConfigurationNode> skillsMap = (Map<Object, ConfigurationNode>) node.getNode("skills").getChildrenMap();

        for(Object skillNode : skillsMap.keySet())
            skills.put(skillNode.toString().toUpperCase(), skillsMap.get(skillNode).getInt(0));
    }

    @Override
    public void serialize(ConfigurationNode node) {
        node.getNode("skills").setValue(skills);
    }

}
