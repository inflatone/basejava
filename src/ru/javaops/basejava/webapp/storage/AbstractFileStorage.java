package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;
import ru.javaops.basejava.webapp.util.ExcUtil;
import ru.javaops.basejava.webapp.util.ValidateUtil;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static ru.javaops.basejava.webapp.util.ValidateUtil.executeAndValidate;

/**
 * File based storage for Resumes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-16
 */
public abstract class AbstractFileStorage extends AbstractStorage<File> {
    private final static String COULD_NOT_CREATE_FILE = "Couldn't create file %s";
    private final static String COULD_NOT_DELETE_FILE = "Couldn't delete file %s";
    private final static String DIRECTORY_READ_ERROR = "Directory read error";
    private final static String FILE_READ_ERROR = "File read error";
    private final static String FILE_WRITE_ERROR = "File write error";

    private final File directory;

    public AbstractFileStorage(String directory) {
        this.directory = ValidateUtil.validateAndGetDirectoryFile(directory);
    }

    protected abstract Resume doRead(InputStream is) throws IOException;

    protected abstract Void doWrite(Resume r, OutputStream os) throws IOException;

    @Override
    protected File getSearchKey(String uuid) {
        return new File(directory, uuid);
    }

    @Override
    protected boolean isExist(File file) {
        return file.exists();
    }

    @Override
    protected void doSave(Resume r, File file) {
        executeAndValidate(
                file::createNewFile,
                result -> result,
                String.format(COULD_NOT_CREATE_FILE, file.getAbsolutePath()),
                file.getName()
        );
        doUpdate(r, file);
    }

    @Override
    protected Resume doGet(File file) {
        return ExcUtil.catchExc(
                () -> doRead(new BufferedInputStream(new FileInputStream(file))), FILE_READ_ERROR, file.getName()
        );
    }

    @Override
    protected void doUpdate(Resume r, File file) {
        ExcUtil.catchExc(
                () -> doWrite(r, new BufferedOutputStream(new FileOutputStream(file))), FILE_WRITE_ERROR, r.getUuid()
        );
    }

    @Override
    protected void doDelete(File file) {
        executeAndValidate(
                file::delete,
                result -> result,
                String.format(COULD_NOT_DELETE_FILE, file.getAbsolutePath()),
                file.getName()
        );
    }

    @Override
    protected Stream<Resume> doGetAllStream() {
        return Arrays.stream(getAll(File::listFiles)).map(this::doGet);
    }

    @Override
    public void clear() {
        Arrays.stream(getAll(File::listFiles)).forEach(this::doDelete);
    }

    @Override
    public int size() {
        return getAll(File::list).length;
    }

    private <T> T[] getAll(Function<File, T[]> directoryFlatMapper) {
        return executeAndValidate(
                () -> directoryFlatMapper.apply(directory),
                Objects::nonNull,
                DIRECTORY_READ_ERROR,
                null
        );
    }
}