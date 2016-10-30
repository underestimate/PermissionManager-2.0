package io.github.djxy.permissionmanager.subjects.special;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.subjects.Permission;
import io.github.djxy.permissionmanager.subjects.Subject;
import io.github.djxy.permissionmanager.subjects.SubjectData;
import io.github.djxy.permissionmanager.subjects.group.Group;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Locatable;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Samuel on 2016-10-13.
 */
public class Default implements org.spongepowered.api.service.permission.Subject {

    private static final Logger LOGGER = new Logger(Default.class);

    public static final String IDENTIFIER = "Default";
    public static final Default instance = new Default();

    private final SubjectData data = new SubjectData(this);
    private final SubjectData transientData = new SubjectData(this);
    private Path file;

    private Default() {
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    @Override
    public Tristate getPermissionValue(Set<Context> set, String permission) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(permission);
        Permission perm = getPermission(this, set, permission);

        /*if(set.equals(Sets.newHashSet(new Context("gp_claim", "0cc71067-d6e6-4f11-90eb-006aa3aa0196")))) {
            System.out.println(permission+" "+data.getContextContainer(set).getPermissions().getPermission(permission));
            System.out.println(permission+" "+perm);
        }*/

        return perm == null?Tristate.UNDEFINED:Tristate.fromBoolean(perm.getValue());
    }

    public Permission getPermission(org.spongepowered.api.service.permission.Subject subject, Set<Context> set, String permission){
        Permission perm;

        if((perm = getPermission(this.getSubjectData(), set, permission)) != null) {
            Subject.logGetPermissionValue(LOGGER, this, set, permission, Tristate.fromBoolean(perm.getValue()));
            return perm;
        }

        if((perm = getPermission(this.getTransientSubjectData(), set, permission)) != null) {
            Subject.logGetPermissionValue(LOGGER, this, set, permission, Tristate.fromBoolean(perm.getValue()));
            return perm;
        }

        if(subject instanceof Locatable && ((Locatable) subject).getWorld() != null){
            Set<Context> worldSet = Sets.newHashSet(new Context(Context.WORLD_KEY, ((Locatable) subject).getWorld().getName()));

            if((perm = getPermission(this.getSubjectData(), worldSet, permission)) != null) {
                Subject.logGetPermissionValue(LOGGER, this, worldSet, permission, Tristate.fromBoolean(perm.getValue()));
                return perm;
            }

            if((perm = getPermission(this.getTransientSubjectData(), worldSet, permission)) != null) {
                Subject.logGetPermissionValue(LOGGER, this, worldSet, permission, Tristate.fromBoolean(perm.getValue()));
                return perm;
            }
        }
        
        if((perm = getPermission(this.getSubjectData(), org.spongepowered.api.service.permission.SubjectData.GLOBAL_CONTEXT, permission)) != null) {
            Subject.logGetPermissionValue(LOGGER, this, org.spongepowered.api.service.permission.SubjectData.GLOBAL_CONTEXT, permission, Tristate.fromBoolean(perm.getValue()));
            return perm;
        }

        if((perm = getPermission(this.getTransientSubjectData(), org.spongepowered.api.service.permission.SubjectData.GLOBAL_CONTEXT, permission)) != null) {
            Subject.logGetPermissionValue(LOGGER, this, org.spongepowered.api.service.permission.SubjectData.GLOBAL_CONTEXT, permission, Tristate.fromBoolean(perm.getValue()));
            return perm;
        }

        Subject.logGetPermissionValue(LOGGER, this, set, permission, Tristate.UNDEFINED);

        return null;
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

    @Override
    public Optional<String> getOption(Set<Context> set, String key) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(key);
        String option;

        if(data.containsContexts(set) && (option = data.getContextContainer(set).getOption(key)) != null) {
            Subject.logGetOption(LOGGER, this, set, key, Optional.of(option));
            return Optional.of(option);
        }

        if(transientData.containsContexts(set) && (option = transientData.getContextContainer(set).getOption(key)) != null) {
            Subject.logGetOption(LOGGER, this, set, key, Optional.of(option));
            return Optional.of(option);
        }

        Subject.logGetOption(LOGGER, this, set, key, Optional.empty());

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

    public void save(){
        File file = this.file.toFile();

        ConfigurationLoader loader = YAMLConfigurationLoader.builder().setIndent(4).setFlowStyle(DumperOptions.FlowStyle.BLOCK).setDefaultOptions(ConfigurationOptions.defaults()).setFile(file).build();
        ConfigurationNode node = loader.createEmptyNode();

        data.serialize(node);

        /*transientData.serialize(node.getNode("transient"));
        data.serialize(node.getNode("persistent"));*/

        try {
            loader.save(node);
            LOGGER.info(IDENTIFIER+": Saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        File file = this.file.toFile();

        try{
            ConfigurationLoader loader = YAMLConfigurationLoader.builder().setIndent(4).setFlowStyle(DumperOptions.FlowStyle.BLOCK).setDefaultOptions(ConfigurationOptions.defaults()).setFile(file).build();
            ConfigurationNode node = loader.load();

            data.deserialize(node);

            //data.deserialize(node.getNode("persistent"));
            
            LOGGER.info(IDENTIFIER+": Loaded.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
