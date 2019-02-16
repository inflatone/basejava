package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.exception.StorageException;
import ru.javaops.basejava.webapp.model.Resume;

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

    protected abstract void doWrite(Resume r, File file) throws IOException;

    @Override
    protected abstract Resume doGet(File file);

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
        try {
            createFile(file);
            doWrite(r, file);
        } catch (IOException e) {
            throw new StorageException("IO Error", file.getName(), e);
        }
    }

    @Override
    protected void doUpdate(Resume r, File file) {
        try {
            doWrite(r, file);
        } catch (IOException e) {
            throw new StorageException("IO Error", file.getName(), e);
        }
    }

    @Override
    protected void doDelete(File file) {
        handleFileOperation(file, File::delete);
    }

    @Override
    protected Stream<Resume> doGetAllStream() {
        return Arrays.stream(Objects.requireNonNull(directory.listFiles())).map(this::doGet);
    }

    @Override
    public void clear() {
        Arrays.stream(Objects.requireNonNull(directory.listFiles())).forEach(f -> handleFileOperation(f, File::delete));
    }

    @Override
    public int size() {
        return Objects.requireNonNull(directory.list()).length;
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

    private void handleFileOperation(File file, Function<File, Boolean> operation) {
        if (!operation.apply(file)) {
            LOG.warning("I/O Error during operating file " + file.getName());
            throw new StorageException("IO Error", file.getName());
        }
    }

    private void createFile(File file) throws IOException {
        if (!file.createNewFile()) {
            LOG.warning("I/O Error during creating file " + file.getName());
            throw new StorageException("IO Error", file.getName());
        }
    }
}
