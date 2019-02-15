package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Array based storage for Resumes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2018-12-18
 */
public class ArrayStorage extends AbstractArrayStorage {

    /**
     * Removes all of the resumes from this storage.
     */
    @Override
    public void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    @Override
    public void update(Resume r) {
        int index = getIndex(r.getUuid());
        if (index == -1) {
            System.out.println("Resume " + r.getUuid() + " is not exist");
        } else {
            storage[index] = r;
        }
    }

    /**
     * Appends the specified resume to the end of the storage.
     *
     * @param r resume to be appended to this storage
     */
    @Override
    public void save(Resume r) {
        if (getIndex(r.getUuid()) != -1) {
            System.out.println("Resume " + r.getUuid() + " already exist");
        } else if (size == STORAGE_LIMIT) {
            System.out.println("Storage overflow");
        } else {
            storage[size++] = r;
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
        if (index == -1) {
            System.out.println("Resume " + uuid + " is not exist");
        } else {
            storage[index] = storage[--size];
            storage[size] = null;
        }
    }

    /**
     * @return array, contains only Resumes in storage (without null)
     */
    @Override
    public Resume[] getAll() {
        return Arrays.copyOf(storage, size);
    }

    /**
     * Returns the index of the specified Resume <tt>uuid</tt>  of which equals the specified one.
     *
     * @param uuid unique number of the resume
     * @return index of the specified resume
     */
    @Override
    protected int getIndex(String uuid) {
        return IntStream.range(0, size)
                .filter(i -> storage[i].getUuid().equals(uuid))
                .findAny()
                .orElse(-1);
    }
}
