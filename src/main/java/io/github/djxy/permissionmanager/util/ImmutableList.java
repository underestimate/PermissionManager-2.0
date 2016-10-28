package io.github.djxy.permissionmanager.util;

import java.util.*;

/**
 * Created by samuelmarchildon-lavoie on 16-10-27.
 */
public class ImmutableList<V> implements List<V> {

    private List<V> list;

    public ImmutableList(List<V> list) {
        this.list = list;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<V> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(V v) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends V> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        list = new ArrayList<>();
    }

    @Override
    public V get(int index) {
        return list.get(index);
    }

    @Override
    public V set(int index, V element) {
        return null;
    }

    @Override
    public void add(int index, V element) {
    }

    @Override
    public V remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<V> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<V> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<V> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
