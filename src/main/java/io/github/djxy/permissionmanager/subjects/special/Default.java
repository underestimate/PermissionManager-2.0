package io.github.djxy.permissionmanager.subjects.special;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.github.djxy.permissionmanager.subjects.ContextContainer;
import io.github.djxy.permissionmanager.subjects.Permission;
import io.github.djxy.permissionmanager.subjects.SubjectListener;
import io.github.djxy.permissionmanager.subjects.group.Group;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.*;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.util.Tristate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-10-13.
 */
public class Default implements org.spongepowered.api.service.permission.Subject {

    public static final String IDENTIFIER = "Default";
    public static final Default instance = new Default();

    private final SubjectData data = new Data();
    private final SubjectData transientData = new Data();

    private Default() {
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return Optional.empty();
    }

    @Override
    public SubjectCollection getContainingCollection() {
        return SpecialCollection.instance;
    }

    @Override
    public SubjectData getSubjectData() {
        return data;
    }

    @Override
    public SubjectData getTransientSubjectData() {
        return transientData;
    }

    @Override
    public boolean hasPermission(Set<Context> set, String s) {
        return getPermissionValue(set, s).asBoolean();
    }

    @Override
    public Tristate getPermissionValue(Set<Context> set, String s) {
        return Tristate.UNDEFINED;
    }

    @Override
    public boolean isChildOf(Set<Context> set, Subject subject) {
        return false;
    }

    @Override
    public List<Subject> getParents(Set<Context> set) {
        return new ArrayList<>();
    }

    @Override
    public Optional<String> getOption(Set<Context> set, String s) {
        return Optional.empty();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<Context> getActiveContexts() {
        return SubjectData.GLOBAL_CONTEXT;
    }

    private class Data implements SubjectData {

        protected final ConcurrentHashMap<Set<Context>, ContextContainer> contexts = new ConcurrentHashMap<>();

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
            }
            else if(contexts.containsKey(set)) {
                ContextContainer container = contexts.get(set);

                container.getPermissions().removePermission(permission);

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

        public Optional<String> getOption(Set<Context> set, String key) {
            if(!contexts.containsKey(set))
                return Optional.empty();

            String value = contexts.get(set).getOption(key);

            if (value == null)
                return Optional.empty();

            return Optional.of(value);
        }

    }
}
