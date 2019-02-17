package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;
import ru.javaops.basejava.webapp.storage.serializer.StreamSerializer;
import ru.javaops.basejava.webapp.util.ExcUtil;

import java.io.*;
import java.util.stream.Stream;

import static ru.javaops.basejava.webapp.util.ValidateUtil.executeAndValidate;

/**
 * Basic class for serialized storages realization
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-17
 */
public abstract class AbstractSerializedStorage<SK> extends AbstractStorage<SK> {
    final static String DIRECTORY_READ_ERROR = "Directory read error";
    private final static String COULD_NOT_CREATE_FILE = "Couldn't create file %s";
    private final static String COULD_NOT_DELETE_FILE = "Couldn't delete file %s";
    private final static String FILE_READ_ERROR = "File read error";
    private final static String FILE_WRITE_ERROR = "File write error";

    private StreamSerializer serializer;

    protected AbstractSerializedStorage(StreamSerializer serializer) {
        this.serializer = serializer;
    }

    protected abstract InputStream newIn(SK file) throws IOException;

    protected abstract OutputStream newOut(SK file) throws IOException;

    protected abstract boolean doCreateFile(SK file) throws IOException;

    protected abstract boolean doDeleteFile(SK file) throws IOException;

    protected abstract String getFileName(SK file);

    protected abstract Stream<SK> getAll();

    @Override
    protected void doSave(Resume r, SK file) {
        executeAndValidate(
                () -> doCreateFile(file),
                result -> result,
                String.format(COULD_NOT_CREATE_FILE, file),
                getFileName(file)
        );
        doUpdate(r, file);
    }

    @Override
    protected Resume doGet(SK file) {
        return ExcUtil.catchExc(
                () -> serializer.doRead(new BufferedInputStream(newIn(file))),
                FILE_READ_ERROR,
                getFileName(file)
        );

    }

    @Override
    protected void doUpdate(Resume r, SK file) {
        ExcUtil.catchExc(
                () -> serializer.doWrite(r, new BufferedOutputStream(newOut(file))),
                FILE_WRITE_ERROR,
                r.getUuid()
        );
    }

    @Override
    protected void doDelete(SK file) {
        executeAndValidate(
                () -> doDeleteFile(file),
                result -> result,
                String.format(COULD_NOT_DELETE_FILE, file),
                getFileName(file)
        );
    }

    @Override
    protected Stream<Resume> doGetAllStream() {
        return getAll().map(this::doGet);
    }

    @Override
    public void clear() {
        getAll().forEach(this::doDelete);
    }

    @Override
    public int size() {
        return (int) getAll().count();
    }


}
