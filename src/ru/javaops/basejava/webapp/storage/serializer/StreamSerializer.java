package ru.javaops.basejava.webapp.storage.serializer;

import ru.javaops.basejava.webapp.model.Resume;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Describes the strategy of serializing resumes.
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-17
 */
public interface StreamSerializer {
    Void doWrite(Resume r, OutputStream os) throws IOException;

    Resume doRead(InputStream is) throws IOException;
}
