package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.exception.ExistStorageException;
import ru.javaops.basejava.webapp.exception.NotExistStorageException;
import ru.javaops.basejava.webapp.model.Resume;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract Storage
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public abstract class AbstractStorage<SK> implements Storage {
    protected static final Logger LOG = Logger.getLogger(AbstractStorage.class.getName());

    /**
     * Returns the search key defined for the specified uuid resume
     *
     * @param uuid uuid of the specified resume
     * @return search key
     */
    protected abstract SK getSearchKey(String uuid);

    /**
     * Checks if the resume specified by search key is contained in the storage
     *
     * @param searchKey search key to define the resume position in storage
     * @return <tt>true</tt> if storage contains resume
     */
    protected abstract boolean isExist(SK searchKey);

    /**
     * Do save operation of new resume.
     * Position of inserting may be found by its specified search key
     *
     * @param r         new resume
     * @param searchKey search key to define the resume position in storage
     */
    protected abstract void doSave(Resume r, SK searchKey);

    /**
     * Do get resume operation by its search key.
     *
     * @param existedSearchKey search key to define the resume position in storage
     * @return correct stored resume
     */
    protected abstract Resume doGet(SK existedSearchKey);

    /**
     * Do update resume operation by its search key
     *
     * @param r         updated version of resume
     * @param searchKey search key to define the resume position in storage
     */
    protected abstract void doUpdate(Resume r, SK searchKey);

    /**
     * Do delete resume operation by its search key
     *
     * @param searchKey search key to define the resume position in storage
     */
    protected abstract void doDelete(SK searchKey);

    /**
     * Returns stream of all the resumes in the storage
     *
     * @return resume stream
     */
    protected abstract Stream<Resume> doGetAllStream();

    @Override
    public void save(Resume r) {
        LOG.info("Delete " + r);
        doSave(r, getNotExistedSearchKey(r.getUuid()));
    }

    @Override
    public Resume get(String uuid) {
        LOG.info("Get " + uuid);
        return doGet(getExistedSearchKey(uuid));
    }

    @Override
    public void update(Resume r) {
        LOG.info("Update " + r);
        doUpdate(r, getExistedSearchKey(r.getUuid()));
    }

    @Override
    public void delete(String uuid) {
        LOG.info("Delete " + uuid);
        doDelete(getExistedSearchKey(uuid));
    }

    @Override
    public List<Resume> getAllSorted() {
        LOG.info("getAllSorted");
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
    private SK getExistedSearchKey(String uuid) {
        SK searchKey = getSearchKey(uuid);
        if (!isExist(searchKey)) {
            LOG.warning("Resume " + uuid + " already exist");
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
    private SK getNotExistedSearchKey(String uuid) {
        SK searchKey = getSearchKey(uuid);
        if (isExist(searchKey)) {
            LOG.warning("Resume " + uuid + " not exist");
            throw new ExistStorageException(uuid);
        }
        return searchKey;
    }
}
