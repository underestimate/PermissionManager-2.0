package io.github.djxy.permissionmanager;

import com.google.common.collect.ImmutableList;
import io.github.djxy.permissionmanager.subjects.special.Default;
import io.github.djxy.permissionmanager.subjects.group.GroupCollection;
import io.github.djxy.permissionmanager.subjects.special.SpecialCollection;
import io.github.djxy.permissionmanager.subjects.user.UserCollection;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Samuel on 2016-08-17.
 */
public class PermissionService implements org.spongepowered.api.service.permission.PermissionService {

    public static final PermissionService instance = new PermissionService();

    private final ConcurrentHashMap<String,SubjectCollection> subjectCollections = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<ContextCalculator> contextCalculators = new CopyOnWriteArrayList<>();

    private PermissionService() {
        subjectCollections.put(UserCollection.instance.getIdentifier(), UserCollection.instance);
        subjectCollections.put(GroupCollection.instance.getIdentifier(), GroupCollection.instance);
        subjectCollections.put(org.spongepowered.api.service.permission.PermissionService.SUBJECTS_SYSTEM, SpecialCollection.instance);
        subjectCollections.put(org.spongepowered.api.service.permission.PermissionService.SUBJECTS_ROLE_TEMPLATE, SpecialCollection.instance);
        subjectCollections.put(org.spongepowered.api.service.permission.PermissionService.SUBJECTS_COMMAND_BLOCK, SpecialCollection.instance);
    }

    @Override
    public SubjectCollection getUserSubjects() {
        return UserCollection.instance;
    }

    @Override
    public SubjectCollection getGroupSubjects() {
        return GroupCollection.instance;
    }

    public SubjectData getDefaultData() {
        return Default.instance.getSubjectData();
    }

    public Subject getDefaults() {
        return Default.instance;
    }

    @Override
    public SubjectCollection getSubjects(String s) {
        return subjectCollections.get(s);
    }

    @Override
    public Map<String, SubjectCollection> getKnownSubjects() {
        return new HashMap<>(subjectCollections);
    }

    @Override
    public Optional<PermissionDescription.Builder> newDescriptionBuilder(Object o) {
        return Optional.empty();
    }

    @Override
    public Optional<PermissionDescription> getDescription(String s) {
        return Optional.empty();
    }

    @Override
    public Collection<PermissionDescription> getDescriptions() {
        return new ArrayList<>();
    }

    @Override
    public void registerContextCalculator(ContextCalculator<Subject> contextCalculator) {
        contextCalculators.add(contextCalculator);
    }

    public List<ContextCalculator> getContextCalculators() {
        return ImmutableList.copyOf(contextCalculators);
    }
}
