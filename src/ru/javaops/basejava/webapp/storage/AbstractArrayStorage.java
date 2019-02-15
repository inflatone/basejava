package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;

/**
 * Array based storage for Resumes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public abstract class AbstractArrayStorage implements Storage {
    protected static final int STORAGE_LIMIT = 100000;
    /**
     * The array buffer into which the resumes are stored.
     */
    protected final Resume[] storage = new Resume[STORAGE_LIMIT];

    /**
     * The number of resumes this storage contains.
     */
    protected int size;

    /**
     * @return the number of resumes this storage contains
     */
    @Override
    public int size() {
        return size;
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
        if (index == -1) {
            System.out.println("Resume " + uuid + " is not exist");
        }
        return index != -1 ? storage[index] : null;
    }

    protected abstract int getIndex(String uuid);
}
