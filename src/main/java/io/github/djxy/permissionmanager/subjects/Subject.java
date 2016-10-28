package io.github.djxy.permissionmanager.subjects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import io.github.djxy.permissionmanager.PermissionService;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.rules.Rule;
import io.github.djxy.permissionmanager.rules.Rules;
import io.github.djxy.permissionmanager.subjects.group.Group;
import io.github.djxy.permissionmanager.subjects.special.Default;
import io.github.djxy.permissionmanager.util.ContextUtil;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Locatable;

import java.util.*;

/**
 * Created by Samuel on 2016-08-09.
 */
public abstract class Subject implements org.spongepowered.api.service.permission.Subject, ConfigurationNodeSerializer, ConfigurationNodeDeserializer {

    private static final Logger LOGGER = new Logger(Subject.class);
    private final static Set<Context> GLOBAL_SET = org.spongepowered.api.service.permission.SubjectData.GLOBAL_CONTEXT;
    private final static List<Group> EMPTY_GROUPS = ImmutableList.of();

    protected String identifier;
    protected final SubjectCollection collection;
    protected final SubjectData data;
    protected final SubjectData transientData;
    private final boolean locatable;
    private final Set<Context> activeContexts = new HashSet<>();
    private long activeContextsLastTick = -1;

    public Subject(String identifier, SubjectCollection collection) {
        Preconditions.checkNotNull(identifier);
        Preconditions.checkNotNull(collection);

        this.identifier = identifier;
        this.collection = collection;
        this.data = new SubjectData(this);
        this.transientData = new SubjectData(this);
        this.locatable = this instanceof Locatable;
    }

    @Override
    public Set<Context> getActiveContexts() {
        int tick = Sponge.getServer().getRunningTimeTicks();

        if(tick != activeContextsLastTick){
            activeContextsLastTick = tick;
            activeContexts.clear();

            for(ContextCalculator contextCalculator : PermissionService.instance.getContextCalculators())
                contextCalculator.accumulateContexts(this, activeContexts);
        }

        return activeContexts;
    }

    @Override
    public Tristate getPermissionValue(Set<Context> set, String s) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(s);
        Permission perm = getPermission(this, set, s);

        if(perm == null)
            perm = Default.instance.getPermission(this, set, s);
        else
            return Tristate.fromBoolean(perm.getValue());

        return perm == null?Tristate.UNDEFINED:Tristate.fromBoolean(perm.getValue());
    }

    protected Permission getPermission(Subject subject, Set<Context> set, String permission){
        Permission perm;

        if((perm = getPermission(this.getTransientSubjectData(), set, permission)) != null) {
            logGetPermissionValue(LOGGER, this, set, permission, Tristate.fromBoolean(perm.getValue()));
            return perm;
        }

        if((perm = getPermission(this.getSubjectData(), set, permission)) != null) {
            logGetPermissionValue(LOGGER, this, set, permission, Tristate.fromBoolean(perm.getValue()));
            return perm;
        }

        if(subject.locatable && ((Locatable) subject).getWorld() != null){
            Set<Context> worldSet = Sets.newHashSet(new Context(Context.WORLD_KEY, ((Locatable) subject).getWorld().getName()));

            if((perm = getPermission(this.getTransientSubjectData(), worldSet, permission)) != null) {
                logGetPermissionValue(LOGGER, this, set, permission, Tristate.fromBoolean(perm.getValue()));
                return perm;
            }

            if((perm = getPermission(this.getSubjectData(), worldSet, permission)) != null) {
                logGetPermissionValue(LOGGER, this, set, permission, Tristate.fromBoolean(perm.getValue()));
                return perm;
            }
        }

        if((perm = getPermission(this.getTransientSubjectData(), GLOBAL_SET, permission)) != null) {
            logGetPermissionValue(LOGGER, this, set, permission, Tristate.fromBoolean(perm.getValue()));
            return perm;
        }

        if((perm = getPermission(this.getSubjectData(), GLOBAL_SET, permission)) != null) {
            logGetPermissionValue(LOGGER, this, set, permission, Tristate.fromBoolean(perm.getValue()));
            return perm;
        }

        logGetPermissionValue(LOGGER, this, set, permission, Tristate.UNDEFINED);

        for(Group group : getGroups(this.getTransientSubjectData(), set))
            if((perm = group.getPermission(subject, set, permission)) != null)
                return perm;

        for(Group group : getGroups(this.getSubjectData(), set))
            if((perm = group.getPermission(subject, set, permission)) != null)
                return perm;

        if(subject.locatable && ((Locatable) subject).getWorld() != null){
            Set<Context> worldSet = Sets.newHashSet(new Context(Context.WORLD_KEY, ((Locatable) subject).getWorld().getName()));

            for(Group group : getGroups(this.getTransientSubjectData(), worldSet))
                if((perm = group.getPermission(subject, set, permission)) != null)
                    return perm;

            for(Group group : getGroups(this.getSubjectData(), worldSet))
                if((perm = group.getPermission(subject, set, permission)) != null)
                    return perm;
        }

        for(Group group : getGroups(this.getTransientSubjectData(), GLOBAL_SET))
            if((perm = group.getPermission(subject, set, permission)) != null)
                return perm;

        for(Group group : getGroups(this.getSubjectData(), GLOBAL_SET))
            if((perm = group.getPermission(subject, set, permission)) != null)
                return perm;

        return null;
    }

    private List<Group> getGroups(SubjectData subjectData, Set<Context> set){
        return subjectData.containsContexts(set)?subjectData.getContextContainer(set).getGroups():EMPTY_GROUPS;
    }

    private Permission getPermission(SubjectData subjectData, Set<Context> set, String permission){
        Permission perm;

        if(subjectData.containsContexts(set)){
            if((perm = subjectData.getContextContainer(set).getPermissions().getPermission(permission)) != null)
                return perm;
        }

        return null;
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

    @Override
    public SubjectCollection getContainingCollection() {
        return collection;
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

    public static void logGetPermissionValue(Logger logger, org.spongepowered.api.service.permission.Subject subject, Set<Context> set, String permission, Tristate tristate){
        logger.info("Subject: "+subject.getIdentifier() +" - Contexts " + set+ " - Permission: " + permission + " - Tristate: "+tristate.name());
    }

    public static void logGetOption(Logger logger, org.spongepowered.api.service.permission.Subject subject, Set<Context> set, String option, Optional<String> value){
        logger.info("Subject: "+subject.getIdentifier() +" - Contexts " + set+ " - Option: " + option + " - Value: "+value);
    }

}
