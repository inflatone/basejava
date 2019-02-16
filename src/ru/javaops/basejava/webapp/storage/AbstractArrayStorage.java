package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.exception.StorageException;
import ru.javaops.basejava.webapp.model.Resume;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Array based storage for Resumes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public abstract class AbstractArrayStorage extends AbstractStorage {
    static final int STORAGE_LIMIT = 10000;

    protected final Resume[] storage = new Resume[STORAGE_LIMIT];

    /**
     * The number of resumes this storage contains.
     */
    int size;

    protected abstract void insertElement(Resume r, int index);

    protected abstract void fillDeletedElement(int index);

    @Override
    protected abstract Integer getSearchKey(String uuid);

    @Override
    protected boolean isExist(Object index) {
        return (int) index >= 0;
    }


    @Override
    protected void doSave(Resume r, Object index) {
        if (size == STORAGE_LIMIT) {
            throw new StorageException("Storage overflow", r.getUuid());
        }
        insertElement(r, (int) index);
        size++;
    }

    @Override
    public Resume doGet(Object index) {
        return storage[(int) index];
    }

    @Override
    protected void doUpdate(Resume r, Object index) {
        storage[(Integer) index] = r;
    }

    @Override
    public void doDelete(Object index) {
        fillDeletedElement((int) index);
        size--;
        storage[size] = null;
    }

    @Override
    protected Stream<Resume> doGetAllStream() {
        return Arrays.stream(storage).limit(size);
    }

    @Override
    public void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }
}
