package io.github.djxy.permissionManager.subjects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.github.djxy.permissionManager.util.ContextUtil;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Samuel on 2016-08-12.
 */
public abstract class SubjectCollection<V extends Subject> implements org.spongepowered.api.service.permission.SubjectCollection {

    protected final String identifier;
    protected final ConcurrentHashMap<String,V> subjects = new ConcurrentHashMap<>();
    protected final Listener subjectListener = new Listener();
    private final ConcurrentHashMap<Context, ConcurrentHashMap<String, ConcurrentHashMap<Subject, Boolean>>> contextsSubjectsWithPermissions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<Subject, Boolean>> globalContextSubjectsWithPermissions = new ConcurrentHashMap<>();

    public SubjectCollection(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public V get(String s) {
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

        if (ContextUtil.isGlobalContext(set))
            subjectWithPermissions = globalContextSubjectsWithPermissions;
        if (ContextUtil.isSingleContext(set)) {
            Context context = ContextUtil.getContext(set);

            if (!contextsSubjectsWithPermissions.containsKey(context))
                return new HashMap<>();

            subjectWithPermissions = contextsSubjectsWithPermissions.get(context);
        }

        if(subjectWithPermissions == null)
            return new HashMap<>();

        if(!subjectWithPermissions.containsKey(s))
            return new HashMap<>();

        return new HashMap<>(subjectWithPermissions.get(s));
    }

    private class Listener implements io.github.djxy.permissionManager.subjects.SubjectListener {

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
