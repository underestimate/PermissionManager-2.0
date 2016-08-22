package io.github.djxy.permissionmanager.rules.time;

import io.github.djxy.permissionmanager.rules.Rule;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Samuel on 2016-08-16.
 */
public class TimeRule implements Rule {

    private final CopyOnWriteArrayList<Lapse> lapses = new CopyOnWriteArrayList<>();

    public void addTimeLapse(Lapse lapse){
        lapses.add(lapse);
    }

    @Override
    public boolean canApply(Player player) {
        for(Lapse lapse : lapses)
            if(lapse.isBetween(player.getWorld()))
                return true;

        return false;
    }

    @Override
    public void apply(Player player) {
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        List<ConfigurationNode> nodes = (List<ConfigurationNode>) node.getChildrenList();

        for(ConfigurationNode n : nodes)
            addTimeLapse(new Lapse(n.getNode("begin").getInt(), n.getNode("end").getInt()));
    }

    @Override
    public void serialize(ConfigurationNode node) {
        List<HashMap> list = new ArrayList<>();

        for(Lapse lapse : lapses) {
            HashMap<String,Integer> values = new HashMap<>();
            values.put("begin", lapse.begin);
            values.put("end", lapse.end);

            list.add(values);
        }

        node.setValue(list);
    }

    public static class Lapse {

        private final int begin;
        private final int end;
        private final boolean beginBigger;

        public Lapse(int begin, int end) {
            this.begin = begin;
            this.end = end;
            this.beginBigger = begin > end;
        }

        public boolean isBetween(World world){
            int worldTime = (int) (world.getProperties().getWorldTime()%24000);

            if(beginBigger)
                return end >= worldTime || worldTime >= begin;
            else
                return end >= worldTime && worldTime >= begin;
        }
    }

}
