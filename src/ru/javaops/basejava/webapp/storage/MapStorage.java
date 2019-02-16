package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;

import java.util.Map;
import java.util.TreeMap;

/**
 * HashMap based storage for Resumes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public class MapStorage extends AbstractStorage {
    private final Map<String, Resume> storage = new TreeMap<>();

    @Override
    protected String getSearchKey(String uuid) {
        return uuid;
    }

    @Override
    protected boolean isExist(Object uuid) {
        String key = (String) uuid;
        return storage.containsKey(key);
    }


    @Override
    public void doSave(Resume r, Object searchKey) {
        storage.put((String) searchKey, r);
    }

    @Override
    public Resume doGet(Object uuid) {
        String key = (String) uuid;
        return storage.get(key);
    }

    @Override
    public void doUpdate(Resume r, Object searchKey) {
        storage.replace((String) searchKey, r);
    }

    @Override
    public void doDelete(Object uuid) {
        String key = (String) uuid;
        storage.remove(key);
    }


    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public Resume[] getAll() {
        return storage.values().toArray(new Resume[0]);
    }

    @Override
    public int size() {
        return storage.size();
    }
}
