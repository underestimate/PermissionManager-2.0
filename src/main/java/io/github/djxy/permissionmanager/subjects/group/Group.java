package io.github.djxy.permissionmanager.subjects.group;

import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.subjects.Subject;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandSource;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Samuel on 2016-08-09.
 */
public class Group extends Subject implements Comparable<Group> {

    private final static Logger LOGGER = new Logger(Group.class);

    private int rank = Integer.MAX_VALUE;
    private boolean defaultGroup = false;
    private final CopyOnWriteArraySet<GroupListener> listeners = new CopyOnWriteArraySet<>();

    protected Group(String identifier, GroupCollection collection) {
        super(identifier, collection);
    }

    public void setRank(int rank) {
        this.rank = rank;

        for(GroupListener listener : listeners)
            listener.onGroupRankChange();
    }

    public int getRank() {
        return rank;
    }

    public boolean isDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(boolean defaultGroup) {
        this.defaultGroup = defaultGroup;

        if(this.defaultGroup)
            for(GroupListener listener : listeners)
                listener.onGroupSetDefault(this);
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return Optional.empty();
    }

    public void addListener(GroupListener listener){
        listeners.add(listener);
    }

    public void removeListener(GroupListener listener){
        listeners.remove(listener);
    }

    protected void delete(){
        for(GroupListener listener : listeners)
            listener.onGroupDeleted(this);
    }

    protected void setIdentifier(String identifier){
        this.identifier = identifier;
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        super.deserialize(node);
        rank = node.getNode("rank").getInt(Integer.MAX_VALUE);
        setDefaultGroup(node.getNode("default").getBoolean(false));
    }

    @Override
    public void serialize(ConfigurationNode node) {
        super.serialize(node);

        if(rank != Integer.MAX_VALUE)
            node.getNode("rank").setValue(rank);

        if(defaultGroup)
            node.getNode("default").setValue(defaultGroup);
    }

    @Override
    public int compareTo(Group o) {
        if(this.rank > o.rank)
            return 1;
        if(this.rank < o.rank)
            return -1;
        else
            return 0;
    }
}
