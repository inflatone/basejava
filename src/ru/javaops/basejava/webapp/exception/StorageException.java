package ru.javaops.basejava.webapp.exception;

import java.io.IOException;

/**
 * Basic exception of storage
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-15
 */
public class StorageException extends RuntimeException {
    private final String uuid;

    public StorageException(String message, String uuid) {
        super(message);
        this.uuid = uuid;

    }

    public StorageException(String message, String uuid, Exception cause) {
        super(message, cause);
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
