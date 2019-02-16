package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.exception.StorageException;
import ru.javaops.basejava.webapp.model.Resume;
import ru.javaops.basejava.webapp.util.ExcUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * File based storage for Resumes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-16
 */
public abstract class AbstractFileStorage extends AbstractStorage<File> {
    private final File directory;

    public AbstractFileStorage(File directory) {
        requireAvailable(directory);
        this.directory = directory;
    }

    protected abstract Resume doRead(File file) throws IOException;

    protected abstract Void doWrite(Resume r, File file) throws IOException;

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
        ExcUtil.catchExc(() -> doCreate(file), "Couldn't create file " + file.getAbsolutePath(), file.getName());
        doUpdate(r, file);
    }

    @Override
    protected Resume doGet(File file) {
        return ExcUtil.catchExc(() -> doRead(file), "File read error", file.getName());
    }

    @Override
    protected void doUpdate(Resume r, File file) {
        ExcUtil.catchExc(() -> doWrite(r, file), "File write error", r.getUuid());
    }

    @Override
    protected void doDelete(File file) {
        checkNullityAndExecute(file, File::delete, "Couldn't delete file " + file.getAbsolutePath(), file.getName());
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

    private void requireAvailable(File directory) {
        Objects.requireNonNull(directory, "directory must not be null");
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory.getAbsolutePath() + " is not directory");
        }
        if (!directory.canRead() || !directory.canWrite()) {
            throw new IllegalArgumentException(directory.getAbsolutePath() + " is not readable/writable");
        }
    }

    private Void doCreate(File file) throws IOException {
        if (!file.createNewFile()) {
            throw new StorageException("Couldn't create file " + file.getAbsolutePath(), file.getName());
        }
        return null;
    }

    private <T> T checkNullityAndExecute(File file, Function<File, T> operation, String excMessage, String excUuid) {
        T result = operation.apply(file);
        if (result == null) {
            throw new StorageException(excMessage, excUuid);
        }
        return result;
    }

    private <T> T[] getAll(Function<File, T[]> directoryFlatMapper) {
        return checkNullityAndExecute(directory, directoryFlatMapper, "Directory read error", null);
    }
}