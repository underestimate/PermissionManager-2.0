package io.github.djxy.permissionManager.subjects;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.github.djxy.permissionManager.subjects.group.Group;
import io.github.djxy.permissionManager.util.ContextUtil;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.util.Tristate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Samuel on 2016-08-09.
 */
public abstract class Subject implements org.spongepowered.api.service.permission.Subject, SubjectData, ConfigurationNodeSerializer, ConfigurationNodeDeserializer {

    protected String identifier;
    protected final SubjectCollection collection;
    protected final ConcurrentHashMap<Context, ContextContainer> contexts;
    private final CopyOnWriteArrayList<SubjectListener> listeners;
    protected ContextContainer globalContext;

    public Subject(String identifier, SubjectCollection collection) {
        Preconditions.checkNotNull(identifier);
        Preconditions.checkNotNull(collection);

        this.collection = collection;
        this.identifier = identifier;
        this.globalContext = new ContextContainer();
        this.contexts = new ConcurrentHashMap<>();
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void addListener(SubjectListener listener){
        listeners.add(listener);
    }

    public void removeListener(SubjectListener listener){
        listeners.remove(listener);
    }

    @Override
    public Set<Context> getActiveContexts() {
        return GLOBAL_CONTEXT;
    }

    @Override
    public SubjectCollection getContainingCollection() {
        return collection;
    }

    @Override
    public SubjectData getSubjectData() {
        return this;
    }

    @Override
    public SubjectData getTransientSubjectData() {
        return this;
    }

    @Override
    public boolean hasPermission(Set<Context> set, String s) {
        return getPermissionValue(set, s).asBoolean();
    }

    @Override
    public boolean hasPermission(String permission) {
        return hasPermission(getActiveContexts(), permission);
    }

    @Override
    public boolean isChildOf(org.spongepowered.api.service.permission.Subject parent) {
        return isChildOf(getActiveContexts(), parent);
    }

    @Override
    public boolean isChildOf(Set<Context> set, org.spongepowered.api.service.permission.Subject subject) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(subject);

        if (!(subject instanceof Group))
            return false;

        Group parent = (Group) subject;

        ContextContainer container = null;

        if (ContextUtil.isGlobalContext(set))
            container = globalContext;
        if (ContextUtil.isSingleContext(set)) {
            Context context = ContextUtil.getContext(set);

            if (!contexts.containsKey(context))
                return false;

            container = contexts.get(context);
        }

        return container != null && container.hasGroup(parent);

    }

    @Override
    public Map<Set<Context>, Map<String, Boolean>> getAllPermissions() {
        Map<Set<Context>, Map<String, Boolean>> map = new HashMap<>();
        Enumeration<Context> contexts = this.contexts.keys();

        while(contexts.hasMoreElements()){
            Context context = contexts.nextElement();

            Set<Context> set = Sets.newHashSet(context);

            map.put(set, getPermissions(set));
        }

        map.put(GLOBAL_CONTEXT, getPermissions(GLOBAL_CONTEXT));

        return map;
    }

    @Override
    public Map<String, Boolean> getPermissions(Set<Context> set) {
        Preconditions.checkNotNull(set);

        ContextContainer container = null;

        if(ContextUtil.isGlobalContext(set))
            container = globalContext;
        if(ContextUtil.isSingleContext(set)){
            Context context = ContextUtil.getContext(set);

            if(!contexts.containsKey(context))
                return new HashMap<>();

            container = contexts.get(context);
        }

        if(container == null)
            return new HashMap<>();

        return container.getPermissions().toMap();
    }

    @Override
    public boolean setPermission(Set<Context> set, String permission, Tristate tristate) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(permission);
        Preconditions.checkNotNull(tristate);

        ContextContainer container = null;

        if(ContextUtil.isGlobalContext(set))
            container = globalContext;
        else if(ContextUtil.isSingleContext(set)){
            Context context = ContextUtil.getContext(set);

            if(!contexts.containsKey(context))
                contexts.put(context, new ContextContainer());

            container = contexts.get(context);
        }

        if(container == null)
            return false;

        if(tristate == Tristate.UNDEFINED) {
            container.getPermissions().removePermission(permission);

            for(SubjectListener listener : listeners)
                listener.onRemovePermission(set, this, permission);
        }
        else {
            container.getPermissions().putPermission(permission, new Permission(permission, tristate.asBoolean()));

            for(SubjectListener listener : listeners)
                listener.onSetPermission(set, this, permission, tristate.asBoolean());
        }

        return true;
    }

    @Override
    public boolean clearPermissions() {
        Enumeration<Context> contexts = this.contexts.keys();

        while(contexts.hasMoreElements())
            clearPermissions(Sets.newHashSet(contexts.nextElement()));

        clearPermissions(GLOBAL_CONTEXT);

        return true;
    }

    @Override
    public boolean clearPermissions(Set<Context> set) {
        Preconditions.checkNotNull(set);

        PermissionMap permissionMap = null;

        if(ContextUtil.isGlobalContext(set))
            permissionMap = globalContext.getPermissions();
        if(ContextUtil.isSingleContext(set)){
            Context context = ContextUtil.getContext(set);

            if(!contexts.containsKey(context))
                return false;


            permissionMap = contexts.get(context).getPermissions();
        }

        if(permissionMap == null)
            return false;

        for(String permission : permissionMap.toMap().keySet())
            for(SubjectListener listener : listeners)
                listener.onRemovePermission(set, this, permission);

        permissionMap.clear();

        return true;
    }

    @Override
    public List<org.spongepowered.api.service.permission.Subject> getParents() {
        return getParents(getActiveContexts());
    }

    @Override
    public Map<Set<Context>, List<org.spongepowered.api.service.permission.Subject>> getAllParents() {
        Map<Set<Context>, List<org.spongepowered.api.service.permission.Subject>> map = new HashMap<>();
        Enumeration<Context> contexts = this.contexts.keys();

        while(contexts.hasMoreElements()){
            Set<Context> set = Sets.newHashSet(contexts.nextElement());

            map.put(set, getParents(set));
        }

        map.put(GLOBAL_CONTEXT, getParents(GLOBAL_CONTEXT));

        return map;
    }

    @Override
    public List<org.spongepowered.api.service.permission.Subject> getParents(Set<Context> set) {
        Preconditions.checkNotNull(set);

        ContextContainer container = null;

        if(ContextUtil.isGlobalContext(set))
            container = globalContext;
        if(ContextUtil.isSingleContext(set)){
            Context context = ContextUtil.getContext(set);

            if(!contexts.containsKey(context))
                return new ArrayList<>();

            container = contexts.get(context);
        }

        return (List) container.getGroups();
    }

    @Override
    public boolean addParent(Set<Context> set, org.spongepowered.api.service.permission.Subject subject) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(subject);

        if(!(subject instanceof Group))
            return false;

        Group parent = (Group) subject;

        ContextContainer container = null;

        if(ContextUtil.isGlobalContext(set))
            container = globalContext;
        if(ContextUtil.isSingleContext(set)){
            Context context = ContextUtil.getContext(set);

            if(!contexts.containsKey(context))
                contexts.put(context, new ContextContainer());

            container = contexts.get(context);
        }

        if(container == null)
            return false;

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

        ContextContainer container = null;

        if(ContextUtil.isGlobalContext(set))
            container = globalContext;
        if(ContextUtil.isSingleContext(set)){
            Context context = ContextUtil.getContext(set);

            if(!contexts.containsKey(context))
                return false;

            container = contexts.get(context);
        }

        if(container == null)
            return false;

        container.removeGroup(parent);

        return true;
    }

    @Override
    public boolean clearParents() {
        Enumeration<Context> contexts = this.contexts.keys();

        while(contexts.hasMoreElements())
            clearParents(Sets.newHashSet(contexts.nextElement()));

        clearParents(GLOBAL_CONTEXT);

        return true;
    }

    @Override
    public boolean clearParents(Set<Context> set) {
        Preconditions.checkNotNull(set);

        if(ContextUtil.isGlobalContext(set))
            globalContext.clearGroups();
        if(ContextUtil.isSingleContext(set)){
            Context context = ContextUtil.getContext(set);

            if(!contexts.containsKey(context))
                return false;

            contexts.get(context).clearGroups();
        }

        return true;
    }

    @Override
    public Map<Set<Context>, Map<String, String>> getAllOptions() {
        Map<Set<Context>, Map<String, String>> options = new HashMap<>();
        Enumeration<Context> contexts = this.contexts.keys();

        while(contexts.hasMoreElements()) {
            Set<Context> set = Sets.newHashSet(contexts.nextElement());

            options.put(set, getOptions(set));
        }

        options.put(GLOBAL_CONTEXT, getOptions(GLOBAL_CONTEXT));

        return options;
    }

    @Override
    public Map<String, String> getOptions(Set<Context> set) {
        Preconditions.checkNotNull(set);

        ContextContainer container = null;

        if(ContextUtil.isGlobalContext(set))
            container = globalContext;
        if(ContextUtil.isSingleContext(set)){
            Context context = ContextUtil.getContext(set);

            if(!contexts.containsKey(context))
                return new HashMap<>();

            container = contexts.get(context);
        }

        if(container == null)
            return new HashMap<>();

        return container.getOptions();
    }

    @Override
    public boolean setOption(Set<Context> set, String key, String value) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(key);

        ContextContainer container = null;

        if(ContextUtil.isGlobalContext(set))
            container = globalContext;
        if(ContextUtil.isSingleContext(set)){
            Context context = ContextUtil.getContext(set);

            if(!contexts.containsKey(context))
                contexts.put(context, new ContextContainer());

            container = contexts.get(context);
        }

        if(container == null)
            return false;

        if(value == null)
            container.removeOption(key);
        else
            container.setOption(key, value);

        return true;
    }

    @Override
    public boolean clearOptions(Set<Context> set) {
        Preconditions.checkNotNull(set);

        ContextContainer container = null;

        if(ContextUtil.isGlobalContext(set))
            container = globalContext;
        if(ContextUtil.isSingleContext(set)){
            Context context = ContextUtil.getContext(set);

            if(!contexts.containsKey(context))
                return true;

            container = contexts.get(context);
        }

        if(container == null)
            return false;

        container.clearOptions();

        return true;
    }

    @Override
    public boolean clearOptions() {
        Enumeration<Context> contexts = this.contexts.keys();

        while(contexts.hasMoreElements())
            clearOptions(Sets.newHashSet(contexts.nextElement()));

        clearOptions(GLOBAL_CONTEXT);

        return true;
    }

    @Override
    public Optional<String> getOption(String key) {
        return getOption(GLOBAL_CONTEXT, key);
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        globalContext = new ContextContainer();

        globalContext.deserialize(node);

        contexts.clear();

        ConfigurationNode worlds = node.getNode("worlds");

        if(!worlds.isVirtual() && worlds.hasMapChildren()){
            Map<Object,ConfigurationNode> worldsMap = (Map<Object, ConfigurationNode>) worlds.getChildrenMap();

            for(Object world : worldsMap.keySet()){
                ContextContainer worldContainer = new ContextContainer();

                worldContainer.deserialize(worldsMap.get(world));

                contexts.put(new Context(Context.WORLD_KEY, world.toString()), worldContainer);
            }
        }
    }

    @Override
    public void serialize(ConfigurationNode node) {
        globalContext.serialize(node);

        Enumeration<Context> enumeration = contexts.keys();

        while(enumeration.hasMoreElements()){
            Context context = enumeration.nextElement();

            if(context.getKey().equals(Context.WORLD_KEY))
                contexts.get(context).serialize(node.getNode("worlds", context.getValue()));
        }
    }

    @Override
    public String toString() {
        return "Subject{" +
                "identifier='" + identifier + '\'' +
                '}';
    }

}
