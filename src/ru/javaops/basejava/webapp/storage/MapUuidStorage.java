package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * HashMap based storage for Resumes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public class MapUuidStorage extends AbstractStorage<String> {
    private final Map<String, Resume> storage = new TreeMap<>();

    @Override
    protected String getSearchKey(String uuid) {
        return uuid;
    }

    @Override
    protected boolean isExist(String uuid) {
        return storage.containsKey(uuid);
    }


    @Override
    public void doSave(Resume r, String searchKey) {
        storage.put(searchKey, r);
    }

    @Override
    public Resume doGet(String uuid) {
        return storage.get(uuid);
    }

    @Override
    public void doUpdate(Resume r, String searchKey) {
        storage.put(searchKey, r);
    }

    @Override
    public void doDelete(String uuid) {
        storage.remove(uuid);
    }

    @Override
    protected Stream<Resume> doGetAllStream() {
        return storage.values().stream();
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public int size() {
        return storage.size();
    }
}
