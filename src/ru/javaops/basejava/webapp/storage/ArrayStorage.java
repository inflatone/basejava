package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;

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

    @Override
    protected void insertElement(Resume r, int index) {
        storage[size] = r;
    }

    @Override
    protected void fillDeletedElement(int index) {
        storage[index] = storage[size - 1];

    }
}
