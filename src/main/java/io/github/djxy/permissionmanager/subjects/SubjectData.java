package io.github.djxy.permissionmanager.subjects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.github.djxy.permissionmanager.subjects.group.Group;
import io.github.djxy.permissionmanager.util.ContextUtil;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Tristate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by samuelmarchildon-lavoie on 16-10-22.
 */
public class SubjectData implements org.spongepowered.api.service.permission.SubjectData, ConfigurationNodeSerializer, ConfigurationNodeDeserializer {

    protected final ConcurrentHashMap<Set<Context>, ContextContainer> contexts = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<SubjectListener> listeners = new CopyOnWriteArraySet<>();
    private final Subject subject;

    public SubjectData(Subject subject) {
        this.subject = subject;
    }

    public Set<Set<Context>> getContexts(){
        return ImmutableSet.copyOf(contexts.keySet());
    }

    public boolean containsContexts(Set<Context> set){
        return contexts.containsKey(set);
    }

    public ContextContainer getContextContainer(Set<Context> set){
        return contexts.containsKey(set)?contexts.get(set):null;
    }

    public void addListener(SubjectListener listener){
        listeners.add(listener);
    }

    public void removeListener(SubjectListener listener){
        listeners.remove(listener);
    }

    @Override
    public Map<Set<Context>, Map<String, Boolean>> getAllPermissions() {
        Map<Set<Context>, Map<String, Boolean>> map = new HashMap<>();
        Enumeration<Set<Context>> contexts = this.contexts.keys();

        while(contexts.hasMoreElements()){
            Set<Context> set = contexts.nextElement();

            map.put(set, getPermissions(set));
        }

        return map;
    }

    @Override
    public Map<String, Boolean> getPermissions(Set<Context> set) {
        Preconditions.checkNotNull(set);

        if(!contexts.containsKey(set))
            return new HashMap<>();

        ContextContainer container = contexts.get(set);

        return container.getPermissions().toMap();
    }

    @Override
    public boolean setPermission(Set<Context> set, String permission, Tristate tristate) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(permission);
        Preconditions.checkNotNull(tristate);

        if(tristate != Tristate.UNDEFINED) {
            if(!contexts.containsKey(set))
                contexts.put(set, new ContextContainer());

            ContextContainer container = contexts.get(set);

            container.getPermissions().putPermission(permission, new Permission(permission, tristate.asBoolean()));

            for(SubjectListener listener : listeners)
                listener.onSetPermission(set, subject, permission, tristate.asBoolean());
        }
        else if(contexts.containsKey(set)) {
            ContextContainer container = contexts.get(set);

            container.getPermissions().removePermission(permission);

            for(SubjectListener listener : listeners)
                listener.onRemovePermission(set, subject, permission);

            if(container.isEmpty())
                contexts.remove(set);
        }

        return true;
    }

    @Override
    public boolean clearPermissions() {
        Enumeration<Set<Context>> contexts = this.contexts.keys();

        while(contexts.hasMoreElements())
            clearPermissions(Sets.newHashSet(contexts.nextElement()));

        return true;
    }

    @Override
    public boolean clearPermissions(Set<Context> set) {
        Preconditions.checkNotNull(set);

        if(!contexts.containsKey(set))
            return false;

        ContextContainer container = contexts.get(set);

        for(String permission : container.getPermissions().toMap().keySet())
            for(SubjectListener listener : listeners)
                listener.onRemovePermission(set, subject, permission);

        container.getPermissions().clear();

        contexts.remove(set);

        return true;
    }

    @Override
    public Map<Set<Context>, List<org.spongepowered.api.service.permission.Subject>> getAllParents() {
        Map<Set<Context>, List<org.spongepowered.api.service.permission.Subject>> map = new HashMap<>();
        Enumeration<Set<Context>> contexts = this.contexts.keys();

        while(contexts.hasMoreElements()){
            Set<Context> set = contexts.nextElement();

            map.put(set, getParents(set));
        }

        return map;
    }

    @Override
    public List<org.spongepowered.api.service.permission.Subject> getParents(Set<Context> set) {
        Preconditions.checkNotNull(set);

        if(!contexts.containsKey(set))
            return new ArrayList<>();

        ContextContainer container = contexts.get(set);


        return (List) container.getGroups();
    }

    @Override
    public boolean addParent(Set<Context> set, org.spongepowered.api.service.permission.Subject subject) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(subject);

        if(!(subject instanceof Group))
            return false;

        Group parent = (Group) subject;

        if(!contexts.containsKey(set))
            contexts.put(set, new ContextContainer());

        ContextContainer container = contexts.get(set);

        container.addGroup(parent);

        return true;
    }

    @Override
    public boolean removeParent(Set<Context> set, org.spongepowered.api.service.permission.Subject subject) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(subject);

        if(!(subject instanceof Group))
            return false;

        Group parent = (Group) subject;

        if(!contexts.containsKey(set))
            return false;

        ContextContainer container = contexts.get(set);

        container.removeGroup(parent);

        if(container.isEmpty())
            contexts.remove(set);

        return true;
    }

    @Override
    public boolean clearParents() {
        Enumeration<Set<Context>> contexts = this.contexts.keys();

        while(contexts.hasMoreElements())
            clearParents(contexts.nextElement());

        return true;
    }

    @Override
    public boolean clearParents(Set<Context> set) {
        Preconditions.checkNotNull(set);

        if(!contexts.containsKey(set))
            return false;

        ContextContainer container = contexts.get(set);

        container.clearGroups();

        if(container.isEmpty())
            contexts.remove(set);

        return true;
    }

    @Override
    public Map<Set<Context>, Map<String, String>> getAllOptions() {
        Map<Set<Context>, Map<String, String>> options = new HashMap<>();
        Enumeration<Set<Context>> contexts = this.contexts.keys();

        while(contexts.hasMoreElements()) {
            Set<Context> set = contexts.nextElement();

            options.put(set, getOptions(set));
        }

        return options;
    }

    @Override
    public Map<String, String> getOptions(Set<Context> set) {
        Preconditions.checkNotNull(set);

        if(!contexts.containsKey(set))
            return new HashMap<>();

        return contexts.get(set).getOptions();
    }

    @Override
    public boolean setOption(Set<Context> set, String key, String value) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(key);

        if(value != null){
            if(!contexts.containsKey(set))
                contexts.put(set, new ContextContainer());
        }
        else if(!contexts.containsKey(set))
            return false;

        ContextContainer container = contexts.get(set);

        if(value == null) {
            container.removeOption(key);

            if(container.isEmpty())
                contexts.remove(set);
        }
        else
            container.setOption(key, value);

        return true;
    }

    @Override
    public boolean clearOptions(Set<Context> set) {
        Preconditions.checkNotNull(set);

        if(!contexts.containsKey(set))
            return false;

        ContextContainer container = contexts.get(set);

        container.clearOptions();

        if(container.isEmpty())
            contexts.remove(set);

        return true;
    }

    @Override
    public boolean clearOptions() {
        Enumeration<Set<Context>> contexts = this.contexts.keys();

        while(contexts.hasMoreElements())
            clearOptions(contexts.nextElement());

        return true;
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        contexts.clear();

        contexts.put(GLOBAL_CONTEXT, new ContextContainer());

        contexts.get(GLOBAL_CONTEXT).deserialize(node);

        ConfigurationNode[] nodes = {node.getNode("worlds")};
        String[] contextKeys = {Context.WORLD_KEY};

        for(int i = 0; i < nodes.length; i++){
            ConfigurationNode contextNode = nodes[i];

            if(!contextNode.isVirtual() && contextNode.hasMapChildren()){
                Map<Object,ConfigurationNode> map = (Map<Object, ConfigurationNode>) contextNode.getChildrenMap();

                for(Object mapNode : map.keySet()){
                    ContextContainer container = new ContextContainer();

                    container.deserialize(map.get(mapNode));

                    contexts.put(Sets.newHashSet(new Context(contextKeys[i], mapNode.toString())), container);
                }
            }
        }

        ConfigurationNode contexts = node.getNode("contexts");

        if(!contexts.isVirtual() && contexts.hasListChildren()){
            List<ConfigurationNode> list = (List<ConfigurationNode>) contexts.getChildrenList();

            for(ConfigurationNode configurationNode : list)
                deserializeContextSet(configurationNode);
        }
    }

    @Override
    public void serialize(ConfigurationNode node) {
        if(contexts.containsKey(GLOBAL_CONTEXT))
            contexts.get(GLOBAL_CONTEXT).serialize(node);

        Enumeration<Set<Context>> enumeration = contexts.keys();

        while(enumeration.hasMoreElements()) {
            Set<Context> set = enumeration.nextElement();

            if(ContextUtil.isSingleContext(set)){
                Context context = ContextUtil.getContext(set);

                if(context.getKey().equals(Context.WORLD_KEY))
                    contexts.get(set).serialize(node.getNode("worlds", context.getValue()));
                else
                    serializeContextSet(node.getNode("contexts").getAppendedNode(), set);
            }
            else if(!ContextUtil.isGlobalContext(set))
                serializeContextSet(node.getNode("contexts").getAppendedNode(), set);
        }
    }

    private void serializeContextSet(ConfigurationNode node, Set<Context> set){
        ConfigurationNode contexts = node.getNode("contexts");

        for(Context context : set)
            contexts.getNode(context.getKey()).setValue(context.getValue());

        this.contexts.get(set).serialize(node);
    }

    private void deserializeContextSet(ConfigurationNode node){
        ConfigurationNode contexts = node.getNode("contexts");
        Set<Context> set = new HashSet<>();

        if(!contexts.isVirtual() && contexts.hasMapChildren()){
            Map<Object,ConfigurationNode> map = (Map<Object, ConfigurationNode>) contexts.getChildrenMap();

            for(Object mapNode : map.keySet())
                set.add(new Context(mapNode.toString(), map.get(mapNode).getString("")));
        }

        if(set.isEmpty())
            return;

        ContextContainer container = new ContextContainer();

        container.deserialize(node);

        this.contexts.put(set, container);
    }

}
