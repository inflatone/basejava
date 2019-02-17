package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Abstract stream storage
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-17
 */
public abstract class AbstractStreamStorage<T> extends AbstractStorage<T> {
    protected final static String COULD_NOT_CREATE_FILE = "Couldn't create file %s";
    protected final static String COULD_NOT_DELETE_FILE = "Couldn't delete file %s";
    protected final static String DIRECTORY_READ_ERROR = "Directory read error";
    protected final static String FILE_READ_ERROR = "Path read error";
    protected final static String FILE_WRITE_ERROR = "Path write error";

    protected abstract Resume doRead(InputStream is) throws IOException;

    protected abstract Void doWrite(Resume r, OutputStream os) throws IOException;
}
