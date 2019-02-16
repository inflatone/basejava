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
public class MapResumeStorage extends AbstractStorage {
    private final Map<String, Resume> storage = new TreeMap<>();

    @Override
    protected Resume getSearchKey(String uuid) {
        return storage.get(uuid);
    }

    @Override
    protected boolean isExist(Object resume) {
        return resume != null;
    }

    @Override
    public void doSave(Resume r, Object resume) {
        storage.put(r.getUuid(), r);
    }

    @Override
    public Resume doGet(Object resume) {
        return (Resume) resume;
    }

    @Override
    public void doUpdate(Resume r, Object searchKey) {
        storage.put(r.getUuid(), r);
    }

    @Override
    public void doDelete(Object resume) {
        storage.remove(((Resume) resume).getUuid());
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
