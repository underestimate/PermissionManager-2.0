package io.github.djxy.permissionManager;

import io.github.djxy.permissionManager.subjects.group.GroupCollection;
import io.github.djxy.permissionManager.subjects.special.SpecialCollection;
import io.github.djxy.permissionManager.subjects.user.UserCollection;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-17.
 */
public class PermissionService implements org.spongepowered.api.service.permission.PermissionService {

    public static final PermissionService instance = new PermissionService();

    private final ConcurrentHashMap<String,SubjectCollection> subjectCollections = new ConcurrentHashMap<>();

    private PermissionService() {
        subjectCollections.put(UserCollection.instance.getIdentifier(), UserCollection.instance);
        subjectCollections.put(GroupCollection.instance.getIdentifier(), GroupCollection.instance);
        subjectCollections.put(org.spongepowered.api.service.permission.PermissionService.SUBJECTS_SYSTEM, new SpecialCollection(org.spongepowered.api.service.permission.PermissionService.SUBJECTS_SYSTEM));
        subjectCollections.put(org.spongepowered.api.service.permission.PermissionService.SUBJECTS_ROLE_TEMPLATE, new SpecialCollection(org.spongepowered.api.service.permission.PermissionService.SUBJECTS_ROLE_TEMPLATE));
        subjectCollections.put(org.spongepowered.api.service.permission.PermissionService.SUBJECTS_COMMAND_BLOCK, new SpecialCollection(org.spongepowered.api.service.permission.PermissionService.SUBJECTS_COMMAND_BLOCK));
    }

    @Override
    public SubjectCollection getUserSubjects() {
        return UserCollection.instance;
    }

    @Override
    public SubjectCollection getGroupSubjects() {
        return GroupCollection.instance;
    }

    @Override
    public Subject getDefaults() {
        return GroupCollection.instance.getDefaults();
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
    }

}
