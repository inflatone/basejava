package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.exception.ExistStorageException;
import ru.javaops.basejava.webapp.exception.NotExistStorageException;
import ru.javaops.basejava.webapp.exception.StorageException;
import ru.javaops.basejava.webapp.model.Resume;

import java.util.Arrays;

/**
 * Array based storage for Resumes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public abstract class AbstractArrayStorage implements Storage {
    private static final int STORAGE_LIMIT = 100000;
    /**
     * The array buffer into which the resumes are stored.
     */
    protected final Resume[] storage = new Resume[STORAGE_LIMIT];

    /**
     * The number of resumes this storage contains.
     */
    int size;

    /**
     * @return the number of resumes this storage contains
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Removes all of the resumes from this storage.
     */
    @Override
    public void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    /**
     * @return array, contains only Resumes in storage (without null)
     */
    @Override
    public Resume[] getAll() {
        return Arrays.copyOf(storage, size);
    }

    @Override
    public void update(Resume r) {
        int index = getIndex(r.getUuid());
        if (index < 0) {
            throw new NotExistStorageException(r.getUuid());
        }
        storage[index] = r;
    }

    @Override
    public void save(Resume r) {
        int index = getIndex(r.getUuid());
        if (index >= 0) {
            throw new ExistStorageException(r.getUuid());
        }
        if (size == STORAGE_LIMIT) {
            throw new StorageException("Storage overflow", r.getUuid());
        } else {
            insertElement(r, index);
            size++;
        }
    }

    /**
     * Removes the resume <tt>uuid</tt> of which equals the specified one.
     *
     * @param uuid unique number of the resume
     */
    @Override
    public void delete(String uuid) {
        int index = getIndex(uuid);
        if (index < 0) {
            throw new ExistStorageException(uuid);
        }
        fillDeletedElement(index);
        size--;
        storage[size] = null;
    }

    /**
     * Returns the resume with the specified uuid
     *
     * @param uuid unique number of the resume
     * @return the resume which uuid equals specified one,
     * or <tt>null</tt> if there was no resume for <tt>uuid</tt>
     */
    @Override
    public Resume get(String uuid) {
        int index = getIndex(uuid);
        if (index < 0) {
            throw new NotExistStorageException(uuid);
        }
        return storage[index];
    }

    protected abstract int getIndex(String uuid);

    protected abstract void insertElement(Resume r, int index);

    protected abstract void fillDeletedElement(int index);
}
