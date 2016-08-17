package io.github.djxy.permissionManager.subjects.user;

import com.google.common.collect.Sets;
import io.github.djxy.permissionManager.subjects.ConfigurationNodeDeserializer;
import io.github.djxy.permissionManager.subjects.ConfigurationNodeSerializer;
import io.github.djxy.permissionManager.subjects.ContextContainer;
import io.github.djxy.permissionManager.subjects.group.Group;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.util.Tristate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-15.
 */
public class Promotion implements ConfigurationNodeSerializer, ConfigurationNodeDeserializer {

    private ContextContainer addGlobalContext = new ContextContainer();
    private ConcurrentHashMap<Context,ContextContainer> addContexts = new ConcurrentHashMap<>();
    private ContextContainer removeGlobalContext = new ContextContainer();
    private ConcurrentHashMap<Context,ContextContainer> removeContexts = new ConcurrentHashMap<>();

    public void promote(User user){
        promoteRemove(user, SubjectData.GLOBAL_CONTEXT, removeGlobalContext);

        for(Map.Entry<Context,ContextContainer> pairs : removeContexts.entrySet())
            promoteRemove(user, Sets.newHashSet(pairs.getKey()), pairs.getValue());

        promoteAdd(user, SubjectData.GLOBAL_CONTEXT, addGlobalContext);

        for(Map.Entry<Context,ContextContainer> pairs : addContexts.entrySet())
            promoteAdd(user, Sets.newHashSet(pairs.getKey()), pairs.getValue());
    }

    private void promoteAdd(User user, Set<Context> set, ContextContainer contextContainer){
        for(Map.Entry<String,Boolean> pairs : contextContainer.getPermissions().toMap().entrySet())
            user.setPermission(set, pairs.getKey(), Tristate.fromBoolean(pairs.getValue()));

        for(Group group : contextContainer.getGroups())
            user.addParent(set, group);

        for(Map.Entry<String,String> pairs : contextContainer.getOptions().entrySet())
            user.setOption(set, pairs.getKey(), pairs.getValue());
    }

    private void promoteRemove(User user, Set<Context> set, ContextContainer contextContainer){
        for(Map.Entry<String,Boolean> pairs : contextContainer.getPermissions().toMap().entrySet())
            user.setPermission(set, pairs.getKey(), Tristate.UNDEFINED);

        for(Group group : contextContainer.getGroups())
            user.removeParent(set, group);

        for(String option : contextContainer.getOptions().keySet())
            user.setOption(set, option, null);
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        addGlobalContext = new ContextContainer();

        addGlobalContext.deserialize(node.getNode("add"));

        addContexts.clear();

        ConfigurationNode worlds = node.getNode("add", "worlds");

        if(!worlds.isVirtual() && worlds.hasMapChildren()){
            Map<Object,ConfigurationNode> worldsMap = (Map<Object, ConfigurationNode>) worlds.getChildrenMap();

            for(Object world : worldsMap.keySet()){
                ContextContainer worldContainer = new ContextContainer();

                worldContainer.deserialize(worldsMap.get(world));

                addContexts.put(new Context(Context.WORLD_KEY, world.toString()), worldContainer);
            }
        }

        removeGlobalContext = new ContextContainer();

        removeGlobalContext.deserialize(node.getNode("remove"));

        removeContexts.clear();

        worlds = node.getNode("remove", "worlds");

        if(!worlds.isVirtual() && worlds.hasMapChildren()){
            Map<Object,ConfigurationNode> worldsMap = (Map<Object, ConfigurationNode>) worlds.getChildrenMap();

            for(Object world : worldsMap.keySet()){
                ContextContainer worldContainer = new ContextContainer();

                worldContainer.deserialize(worldsMap.get(world));

                removeContexts.put(new Context(Context.WORLD_KEY, world.toString()), worldContainer);
            }
        }
    }

    @Override
    public void serialize(ConfigurationNode node) {
        addGlobalContext.serialize(node.getNode("add"));

        for(Map.Entry<Context,ContextContainer> pairs : addContexts.entrySet()){
            if(pairs.getKey().getKey().equals(Context.WORLD_KEY))
                pairs.getValue().serialize(node.getNode("add", "worlds", pairs.getKey().getName()));
        }

        removeGlobalContext.serialize(node.getNode("remove"));

        for(Map.Entry<Context,ContextContainer> pairs : removeContexts.entrySet()){
            if(pairs.getKey().getKey().equals(Context.WORLD_KEY))
                pairs.getValue().serialize(node.getNode("remove", "worlds", pairs.getKey().getName()));
        }
    }
}
