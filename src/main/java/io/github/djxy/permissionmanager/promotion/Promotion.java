package io.github.djxy.permissionmanager.promotion;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.subjects.ConfigurationNodeDeserializer;
import io.github.djxy.permissionmanager.subjects.ConfigurationNodeSerializer;
import io.github.djxy.permissionmanager.subjects.ContextContainer;
import io.github.djxy.permissionmanager.subjects.group.Group;
import io.github.djxy.permissionmanager.subjects.user.User;
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
public final class Promotion implements ConfigurationNodeSerializer, ConfigurationNodeDeserializer {

    private static final Logger LOGGER = new Logger(Promotion.class);

    private ContextContainer addGlobalContext = new ContextContainer();
    private ConcurrentHashMap<Context,ContextContainer> addContexts = new ConcurrentHashMap<>();
    private ContextContainer removeGlobalContext = new ContextContainer();
    private ConcurrentHashMap<Context,ContextContainer> removeContexts = new ConcurrentHashMap<>();
    private String name;

    protected Promotion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public ContextContainer getAddGlobalContextContainer(){
        return addGlobalContext;
    }

    public ContextContainer getRemoveGlobalContextContainer(){
        return removeGlobalContext;
    }

    public ContextContainer getAddContextContainer(Context context){
        Preconditions.checkNotNull(context);

        return addContexts.get(context);
    }

    public ContextContainer getRemoveContextContainer(Context context){
        Preconditions.checkNotNull(context);

        return removeContexts.get(context);
    }

    public boolean containsAddContext(Context context){
        Preconditions.checkNotNull(context);

        return addContexts.containsKey(context);
    }

    public boolean containsRemoveContext(Context context){
        Preconditions.checkNotNull(context);

        return removeContexts.containsKey(context);
    }

    public ContextContainer createAddContext(Context context){
        Preconditions.checkNotNull(context);

        if(addContexts.containsKey(context))
            return addContexts.get(context);

        ContextContainer container = new ContextContainer();

        addContexts.put(context, container);

        return container;
    }

    public ContextContainer createRemoveContext(Context context){
        Preconditions.checkNotNull(context);

        if(removeContexts.containsKey(context))
            return removeContexts.get(context);

        ContextContainer container = new ContextContainer();

        removeContexts.put(context, container);

        return container;
    }

    public void promote(User user){
        LOGGER.info("Promotion "+getName()+" apply to "+user.getIdentifier()+".");
        promoteRemove(user, SubjectData.GLOBAL_CONTEXT, removeGlobalContext);

        for(Map.Entry<Context,ContextContainer> pairs : removeContexts.entrySet())
            promoteRemove(user, Sets.newHashSet(pairs.getKey()), pairs.getValue());

        promoteAdd(user, SubjectData.GLOBAL_CONTEXT, addGlobalContext);

        for(Map.Entry<Context,ContextContainer> pairs : addContexts.entrySet())
            promoteAdd(user, Sets.newHashSet(pairs.getKey()), pairs.getValue());
    }

    private void promoteAdd(User user, Set<Context> set, ContextContainer contextContainer){
        for(Map.Entry<String,Boolean> pairs : contextContainer.getPermissions().toMap().entrySet())
            user.getSubjectData().setPermission(set, pairs.getKey(), Tristate.fromBoolean(pairs.getValue()));

        for(Group group : contextContainer.getGroups())
            user.getSubjectData().addParent(set, group);

        for(Map.Entry<String,String> pairs : contextContainer.getOptions().entrySet())
            user.getSubjectData().setOption(set, pairs.getKey(), pairs.getValue());
    }

    private void promoteRemove(User user, Set<Context> set, ContextContainer contextContainer){
        for(Map.Entry<String,Boolean> pairs : contextContainer.getPermissions().toMap().entrySet())
            user.getSubjectData().setPermission(set, pairs.getKey(), Tristate.UNDEFINED);

        for(Group group : contextContainer.getGroups())
            user.getSubjectData().removeParent(set, group);

        for(String option : contextContainer.getOptions().keySet())
            user.getSubjectData().setOption(set, option, null);
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
