package io.github.djxy.permissionmanager.subjects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierExistException;
import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierInvalidException;
import io.github.djxy.permissionmanager.logger.Logger;
import io.github.djxy.permissionmanager.util.ContextUtil;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-12.
 */
public abstract class SubjectCollection implements org.spongepowered.api.service.permission.SubjectCollection {

    private static final Logger LOGGER = new Logger(SubjectCollection.class);

    protected final String identifier;
    protected final String subjectName;
    protected final ConcurrentHashMap<String,Subject> subjects = new ConcurrentHashMap<>();
    protected final Listener subjectListener = new Listener();
    private final ConcurrentHashMap<Context, ConcurrentHashMap<String, ConcurrentHashMap<Subject, Boolean>>> contextsSubjectsWithPermissions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<Subject, Boolean>> globalContextSubjectsWithPermissions = new ConcurrentHashMap<>();
    protected Path directory;

    abstract protected Subject createSubjectFromFile(String identifier) throws SubjectIdentifierInvalidException, SubjectIdentifierExistException;

    public SubjectCollection(String identifier, String subjectName) {
        Preconditions.checkNotNull(identifier);
        Preconditions.checkNotNull(subjectName);

        this.identifier = identifier;
        this.subjectName = subjectName;
    }

    public void setDirectory(Path directory) {
        Preconditions.checkNotNull(directory);

        this.directory = directory;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public Subject getDefaults() {
        return null;
    }

    @Override
    public Subject get(String s) {
        Preconditions.checkNotNull(s);

        return subjects.get(s);
    }

    @Override
    public boolean hasRegistered(String s) {
        return subjects.containsKey(s);
    }

    @Override
    public Iterable<Subject> getAllSubjects() {
        return ImmutableList.copyOf(subjects.values());
    }

    @Override
    public Map<Subject, Boolean> getAllWithPermission(String s) {
        Preconditions.checkNotNull(s);

        if(!globalContextSubjectsWithPermissions.containsKey(s))
            return new HashMap<>();

        return new HashMap<>(globalContextSubjectsWithPermissions.get(s));
    }

    @Override
    public Map<Subject, Boolean> getAllWithPermission(Set<Context> set, String s) {
        Preconditions.checkNotNull(set);
        Preconditions.checkNotNull(s);

        ConcurrentHashMap<String, ConcurrentHashMap<Subject, Boolean>> subjectWithPermissions = null;

        if (ContextUtil.isGlobalContext(set)) {
            subjectWithPermissions = globalContextSubjectsWithPermissions;
            LOGGER.info("Get all subjects with permission "+s+" global context.");
        }
        if (ContextUtil.isSingleContext(set)) {
            Context context = ContextUtil.getContext(set);

            if (!contextsSubjectsWithPermissions.containsKey(context))
                return new HashMap<>();

            subjectWithPermissions = contextsSubjectsWithPermissions.get(context);
            LOGGER.info("Get all subjects with permission "+s+" "+context.getKey()+"("+context.getValue()+").");
        }

        if(subjectWithPermissions == null)
            return new HashMap<>();

        if(!subjectWithPermissions.containsKey(s))
            return new HashMap<>();

        return new HashMap<>(subjectWithPermissions.get(s));
    }

    public synchronized void load(){
        File files[] = this.directory.toFile().listFiles();

        if(files == null)
            return;

        for(File file : files)
            if(file.getName().contains("."))
                load(file.getName().substring(0, file.getName().lastIndexOf(".")));
    }

    public boolean canLoadSubject(String identifier){
        Preconditions.checkNotNull(identifier);

        File file = directory.resolve(identifier + ".yml").toFile();

        return file.exists();
    }

    public synchronized boolean load(String identifier) {
        Preconditions.checkNotNull(identifier);

        directory.toFile().mkdirs();

        File file = directory.resolve(identifier+".yml").toFile();

        if(!file.exists())
            return false;

        io.github.djxy.permissionmanager.subjects.Subject subject;

        LOGGER.info(subjectName+": " + identifier + " - Loading started.");

        if(subjects.containsKey(identifier))
            subject = (io.github.djxy.permissionmanager.subjects.Subject) subjects.get(identifier);
        else{
            try{
                subject = (io.github.djxy.permissionmanager.subjects.Subject) createSubjectFromFile(identifier);
            }catch (Exception e){
                LOGGER.error(subjectName +": " + identifier + " - Loading failed.");
                e.printStackTrace();
                return false;
            }
        }

        try{
            ConfigurationLoader loader = YAMLConfigurationLoader.builder().setIndent(4).setFlowStyle(DumperOptions.FlowStyle.BLOCK).setDefaultOptions(ConfigurationOptions.defaults()).setFile(file).build();
            ConfigurationNode node = loader.load();

            subject.deserialize(node);
        } catch (Exception e) {
            LOGGER.error(subjectName + ": " + identifier + " - Loading failed.");
            e.printStackTrace();
            return false;
        }

        LOGGER.info(subjectName+": " + identifier + " - Loaded.");
        return true;
    }

    public synchronized void save(){
        Enumeration<String> identifiers = subjects.keys();

        while(identifiers.hasMoreElements()) {
            try {
                save(identifiers.nextElement());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void save(String identifier) throws IOException {
        Preconditions.checkNotNull(identifier);

        directory.toFile().mkdirs();

        File file = directory.resolve(identifier+".yml").toFile();

        if(!subjects.containsKey(identifier))
            return;

        io.github.djxy.permissionmanager.subjects.Subject subject = (io.github.djxy.permissionmanager.subjects.Subject) subjects.get(identifier);

        ConfigurationLoader loader = YAMLConfigurationLoader.builder().setIndent(4).setFlowStyle(DumperOptions.FlowStyle.BLOCK).setDefaultOptions(ConfigurationOptions.defaults()).setFile(file).build();
        ConfigurationNode node = loader.createEmptyNode();

        subject.serialize(node);

        loader.save(node);

        LOGGER.info(subjectName+": " + identifier + " - Saved.");
    }

    private class Listener implements io.github.djxy.permissionmanager.subjects.SubjectListener {

        @Override
        public void onSetPermission(Set<Context> set, Subject subject, String permission, boolean value) {
            Preconditions.checkNotNull(set);
            Preconditions.checkNotNull(permission);
            Preconditions.checkNotNull(value);

            ConcurrentHashMap<String, ConcurrentHashMap<Subject, Boolean>> subjectWithPermissions = null;

            if (ContextUtil.isGlobalContext(set))
                subjectWithPermissions = globalContextSubjectsWithPermissions;
            if (ContextUtil.isSingleContext(set)) {
                Context context = ContextUtil.getContext(set);

                if (!contextsSubjectsWithPermissions.containsKey(context))
                    contextsSubjectsWithPermissions.put(context, new ConcurrentHashMap<>());

                subjectWithPermissions = contextsSubjectsWithPermissions.get(context);
            }

            if(subjectWithPermissions == null)
                return;

            if(!subjectWithPermissions.containsKey(permission))
                subjectWithPermissions.put(permission, new ConcurrentHashMap<>());

            subjectWithPermissions.get(permission).put(subject, value);
        }

        @Override
        public void onRemovePermission(Set<Context> set, Subject subject, String permission) {
            Preconditions.checkNotNull(set);
            Preconditions.checkNotNull(permission);

            ConcurrentHashMap<String, ConcurrentHashMap<Subject, Boolean>> subjectWithPermissions = null;

            if (ContextUtil.isGlobalContext(set))
                subjectWithPermissions = globalContextSubjectsWithPermissions;
            if (ContextUtil.isSingleContext(set)) {
                Context context = ContextUtil.getContext(set);

                if (!contextsSubjectsWithPermissions.containsKey(context))
                    return;

                subjectWithPermissions = contextsSubjectsWithPermissions.get(context);
            }

            if(subjectWithPermissions == null)
                return;

            if(!subjectWithPermissions.containsKey(permission))
                return;

            subjectWithPermissions.get(permission).remove(subject);
        }

    }

}
