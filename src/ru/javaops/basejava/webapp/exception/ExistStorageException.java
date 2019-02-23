package ru.javaops.basejava.webapp.exception;

/**
 * Thrown when try to add already existed resume.
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public class ExistStorageException extends StorageException {
    public ExistStorageException(String uuid) {
        super("Resume " + uuid + " already exist", uuid);
    }
}
