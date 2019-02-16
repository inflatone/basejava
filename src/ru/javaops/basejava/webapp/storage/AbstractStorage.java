package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.exception.ExistStorageException;
import ru.javaops.basejava.webapp.exception.NotExistStorageException;
import ru.javaops.basejava.webapp.model.Resume;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract Storage
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public abstract class AbstractStorage implements Storage {
    /**
     * Returns the search key defined for the specified uuid resume
     *
     * @param uuid uuid of the specified resume
     * @return search key
     */
    protected abstract Object getSearchKey(String uuid);

    /**
     * Checks if the resume specified by search key is contained in the storage
     *
     * @param searchKey search key to define the resume position in storage
     * @return <tt>true</tt> if storage contains resume
     */
    protected abstract boolean isExist(Object searchKey);

    /**
     * Do save operation of new resume.
     * Position of inserting may be found by its specified search key
     *
     * @param r         new resume
     * @param searchKey search key to define the resume position in storage
     */
    protected abstract void doSave(Resume r, Object searchKey);

    /**
     * Do get resume operation by its search key.
     *
     * @param existedSearchKey search key to define the resume position in storage
     * @return correct stored resume
     */
    protected abstract Resume doGet(Object existedSearchKey);

    /**
     * Do update resume operation by its search key
     *
     * @param r         updated version of resume
     * @param searchKey search key to define the resume position in storage
     */
    protected abstract void doUpdate(Resume r, Object searchKey);

    /**
     * Do delete resume operation by its search key
     *
     * @param searchKey search key to define the resume position in storage
     */
    protected abstract void doDelete(Object searchKey);

    /**
     * Returns stream of all the resumes in the storage
     *
     * @return resume stream
     */
    protected abstract Stream<Resume> doGetAllStream();

    @Override
    public void save(Resume r) {
        doSave(r, getNotExistedSearchKey(r.getUuid()));
    }

    @Override
    public Resume get(String uuid) {
        return doGet(getExistedSearchKey(uuid));
    }

    @Override
    public void update(Resume r) {
        doUpdate(r, getExistedSearchKey(r.getUuid()));
    }

    @Override
    public void delete(String uuid) {
        doDelete(getExistedSearchKey(uuid));
    }

    @Override
    public List<Resume> getAllSorted() {
        return doGetAllStream()
                .sorted()
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    /**
     * Conducts required check of resume existence
     *
     * @param uuid uuid of specified resume
     * @return search key to handle read-update-delete operations with the storage
     * @throws NotExistStorageException if resume with this uuid not found in storage
     */
    private Object getExistedSearchKey(String uuid) {
        Object searchKey = getSearchKey(uuid);
        if (!isExist(searchKey)) {
            throw new NotExistStorageException(uuid);
        }
        return searchKey;
    }

    /**
     * Conducts required check of resume nonexistence
     *
     * @param uuid uuid of specified resume
     * @return search key to handle create operations with the storage
     * @throws ExistStorageException if resume with this uuid already stored
     */
    private Object getNotExistedSearchKey(String uuid) {
        Object searchKey = getSearchKey(uuid);
        if (isExist(searchKey)) {
            throw new ExistStorageException(uuid);
        }
        return searchKey;
    }
}
