package io.github.djxy.permissionmanager.subjects.special;

import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierExistException;
import io.github.djxy.permissionmanager.exceptions.SubjectIdentifierInvalidException;
import io.github.djxy.permissionmanager.subjects.SubjectCollection;
import org.spongepowered.api.service.permission.Subject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by Samuel on 2016-08-17.
 */
public class SpecialCollection implements org.spongepowered.api.service.permission.SubjectCollection {

    private static final String IDENTIFIER = "Special";

    public final static SpecialCollection instance = new SpecialCollection();

    private SpecialCollection() {
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Subject get(String s) {
        if(s.equals(Default.IDENTIFIER))
            return Default.instance;

        return new Special(s, this);
    }

    @Override
    public boolean hasRegistered(String s) {
        return true;
    }

    @Override
    public Iterable<Subject> getAllSubjects() {
        return Arrays.asList(Default.instance);
    }

    @Override
    public Map<Subject, Boolean> getAllWithPermission(String s) {
        return new HashMap<>();
    }

    @Override
    public Map<Subject, Boolean> getAllWithPermission(Set set, String s) {
        return new HashMap<>();
    }

    @Override
    public Subject getDefaults() {
        return new Special("", this);
    }

}
