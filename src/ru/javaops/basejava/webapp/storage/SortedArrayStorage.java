package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;

import java.util.Arrays;

/**
 * Sorted array based storage
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public class SortedArrayStorage extends AbstractArrayStorage {
    @Override
    protected int getIndex(String uuid) {
         return Arrays.binarySearch(storage, 0, size, new Resume(uuid));
    }

    @Override
    protected void insertElement(Resume r, int index) {
        index = -index - 1;
        System.arraycopy(storage, index, storage, index + 1, size - index);
        storage[index] = r;
    }

    @Override
    protected void fillDeletedElement(int index) {
        int amountToMove = size - index - 1;
        if (amountToMove > 0) {
            System.arraycopy(storage, index + 1, storage, index, amountToMove);
        }
    }
}