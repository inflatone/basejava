package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;

/**
 * Storage interface for implementing with all storage realizations
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public interface Storage {
    /**
     * Removes all of the resumes from this storage.
     */
    void clear();

    void update(Resume r);

    /**
     * Appends the specified resume to the end of the storage.
     *
     * @param r resume to be appended to this storage
     */
    void save(Resume r);

    /**
     * Returns the resume with the specified uuid
     *
     * @param uuid unique number of the resume
     * @return the resume which uuid equals specified one,
     * or <tt>null</tt> if there was no resume for <tt>uuid</tt>
     */
    Resume get(String uuid);

    /**
     * Removes the resume <tt>uuid</tt> of which equals the specified one.
     *
     * @param uuid unique number of the resume
     */
    void delete(String uuid);

    /**
     * @return array, contains only Resumes in storage (without null)
     */
    Resume[] getAll();

    /**
     * @return the number of resumes this storage contains
     */
    int size();
}
