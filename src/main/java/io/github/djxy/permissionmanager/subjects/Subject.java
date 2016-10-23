package io.github.djxy.permissionmanager.subjects;

import com.google.common.base.Preconditions;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.subjects.group.Group;
import io.github.djxy.permissionmanager.util.ContextUtil;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.util.Tristate;

import java.util.*;

/**
 * Created by Samuel on 2016-08-09.
 */
public abstract class Subject implements org.spongepowered.api.service.permission.Subject, ConfigurationNodeSerializer, ConfigurationNodeDeserializer {

    private static final Logger LOGGER = new Logger(Subject.class);

    protected String identifier;
    protected final SubjectCollection collection;
    protected final SubjectData data;
    protected final SubjectData transientData;

    public Subject(String identifier, SubjectCollection collection) {
        Preconditions.checkNotNull(identifier);
        Preconditions.checkNotNull(collection);

        this.identifier = identifier;
        this.collection = collection;
        this.data = new SubjectData(this);
        this.transientData = new SubjectData(this);
    }

    @Override
    public List<org.spongepowered.api.service.permission.Subject> getParents() {
        return getParents(org.spongepowered.api.service.permission.SubjectData.GLOBAL_CONTEXT);
    }

    @Override
    public List<org.spongepowered.api.service.permission.Subject> getParents(Set<Context> set) {
        ArrayList<org.spongepowered.api.service.permission.Subject> groups = new ArrayList<>();

        groups.addAll(getSubjectData().getParents(set));
        groups.addAll(getTransientSubjectData().getParents(set));

        return groups;
    }

    public Collection<Context> getWorldContexts(){
        ArrayList<Context> worldContexts = new ArrayList<>();

        for(Set<Context> contextSet : data.getContexts()) {
            if (ContextUtil.isSingleContext(contextSet)) {
                Context context = ContextUtil.getContext(contextSet);

                if (context.getKey().equals(Context.WORLD_KEY))
                    worldContexts.add(context);
            }
        }

        return worldContexts;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void addListener(SubjectListener listener){
        data.addListener(listener);
        transientData.addListener(listener);
    }

    public void removeListener(SubjectListener listener){
        data.removeListener(listener);
        transientData.removeListener(listener);
    }

    @Override
    public Set<Context> getActiveContexts() {
        return SubjectData.GLOBAL_CONTEXT;
    }

    @Override
    public SubjectCollection getContainingCollection() {
        return collection;
    }

    @Override
    public org.spongepowered.api.service.permission.SubjectData getSubjectData() {
        return data;
    }

    @Override
    public org.spongepowered.api.service.permission.SubjectData getTransientSubjectData() {
        return transientData;
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

        return subject instanceof Group && (data.getParents(set).contains(subject) || transientData.getParents(set).contains(subject));

    }

    @Override
    public Optional<String> getOption(String key) {
        return getOption(org.spongepowered.api.service.permission.SubjectData.GLOBAL_CONTEXT, key);
    }

    @Override
    public void deserialize(ConfigurationNode node) {
        data.deserialize(node);
    }

    @Override
    public void serialize(ConfigurationNode node) {
        data.serialize(node);
    }

    @Override
    public String toString() {
        return "Subject{" +
                "identifier='" + identifier + '\'' +
                '}';
    }

    public static void logGetPermissionValue(Logger logger, Subject subject, Set<Context> set, String permission, Tristate tristate){
        logger.info("Subject: "+subject.getIdentifier() +" - Contexts " + set+ " - Permission: " + permission + " - Tristate: "+tristate.name());
    }

    public static void logGetOption(Logger logger, Subject subject, Set<Context> set, String option, Optional<String> value){
        logger.info("Subject: "+subject.getIdentifier() +" - Contexts " + set+ " - Option: " + option + " - Value: "+value);
    }

}
