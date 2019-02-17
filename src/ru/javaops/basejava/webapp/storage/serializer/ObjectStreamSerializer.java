package ru.javaops.basejava.webapp.storage.serializer;

import ru.javaops.basejava.webapp.exception.StorageException;
import ru.javaops.basejava.webapp.model.Resume;

import java.io.*;

/**
 * Object stream serialization strategy
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-17
 */
public class ObjectStreamSerializer implements StreamSerializer {
    @Override
    public Resume doRead(InputStream is) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return (Resume) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new StorageException("Error read resume", null, e);
        }
    }

    @Override
    public Void doWrite(Resume r, OutputStream os) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(r);
        }
        return null;
    }
}
