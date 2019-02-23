package ru.javaops.basejava.webapp.exception;

/**
 * Thrown when try to edit not existed resume.
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public class NotExistStorageException extends StorageException {
    public NotExistStorageException(String uuid) {
        super("Resume " + uuid + " not exist", uuid);
    }
}