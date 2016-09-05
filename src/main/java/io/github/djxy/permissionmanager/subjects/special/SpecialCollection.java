package io.github.djxy.permissionmanager.subjects.special;

import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierExistException;
import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierInvalidException;
import io.github.djxy.permissionmanager.subjects.SubjectCollection;
import org.spongepowered.api.service.permission.Subject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Samuel on 2016-08-17.
 */
public class SpecialCollection extends SubjectCollection {

    public SpecialCollection(String identifier) {
        super(identifier, "Special");
    }

    @Override
    public String getIdentifier() {
        return super.getIdentifier();
    }

    @Override
    public Subject get(String s) {
        return new Special(s, this);
    }

    @Override
    public boolean hasRegistered(String s) {
        return super.hasRegistered(s);
    }

    @Override
    public Iterable<Subject> getAllSubjects() {
        return new ArrayList<>();
    }

    @Override
    public Map<Subject, Boolean> getAllWithPermission(String s) {
        return new HashMap<>();
    }

    @Override
    public Map<Subject, Boolean> getAllWithPermission(Set set, String s) {
        return new HashMap<>();
    }

    public Subject getDefaults() {
        return new Special("", this);
    }

    @Override
    public synchronized void save(String identifier) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void save() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized boolean load(String identifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void load() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDirectory(Path directory) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Subject createSubjectFromFile(String identifier) throws SubjectIdentifierInvalidException, SubjectIdentifierExistException {
        throw new UnsupportedOperationException();
    }

}
