package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * ArrayList based storage for Resumes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public class ListStorage extends AbstractStorage {
    private final List<Resume> storage = new ArrayList<>();

    @Override
    protected Integer getSearchKey(String uuid) {
        return IntStream.range(0, storage.size())
                .filter(i -> storage.get(i).getUuid().equals(uuid))
                .boxed()
                .findFirst()
                .orElse(null);
    }

    @Override
    protected boolean isExist(Object index) {
        return index != null;
    }


    @Override
    public void doSave(Resume r, Object index) {
        storage.add(r);
    }

    @Override
    public Resume doGet(Object index) {
        return storage.get((int) index);
    }

    @Override
    public void doUpdate(Resume r, Object index) {
        storage.set((int) index, r);
    }

    @Override
    public void doDelete(Object index) {
        storage.remove((int) index);
    }

    @Override
    protected Stream<Resume> doGetAllStream() {
        return storage.stream();
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
