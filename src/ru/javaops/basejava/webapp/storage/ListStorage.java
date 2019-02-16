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
public class ListStorage extends AbstractStorage<Integer> {
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
    protected boolean isExist(Integer index) {
        return index != null;
    }


    @Override
    public void doSave(Resume r, Integer index) {
        storage.add(r);
    }

    @Override
    public Resume doGet(Integer index) {
        return storage.get(index);
    }

    @Override
    public void doUpdate(Resume r, Integer index) {
        storage.set(index, r);
    }

    @Override
    public void doDelete(Integer index) {
        storage.remove(index.intValue());
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
