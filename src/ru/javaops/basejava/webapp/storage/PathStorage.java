package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;
import ru.javaops.basejava.webapp.storage.serializer.StreamSerializer;
import ru.javaops.basejava.webapp.util.ValidateUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static ru.javaops.basejava.webapp.util.ExcUtil.catchExc;
import static ru.javaops.basejava.webapp.util.ValidateUtil.executeAndValidate;

/**
 * Path based storage for Resumes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-16
 */
public class PathStorage extends AbstractStorage<Path> {
    private final static String COULD_NOT_CREATE_FILE = "Couldn't create file %s";
    private final static String COULD_NOT_DELETE_FILE = "Couldn't delete file %s";
    private final static String DIRECTORY_READ_ERROR = "Directory read error";
    private final static String FILE_READ_ERROR = "Path read error";
    private final static String FILE_WRITE_ERROR = "Path write error";

    private final Path directory;
    private final StreamSerializer serializer;

    public PathStorage(String directory, StreamSerializer serializer) {
        this.directory = ValidateUtil.validateAndGetDirectoryPath(directory);
        this.serializer = serializer;
    }

    @Override
    protected Path getSearchKey(String uuid) {
        return directory.resolve(uuid);
    }

    @Override
    protected boolean isExist(Path file) {
        return Files.isRegularFile(file);
    }

    @Override
    protected void doSave(Resume r, Path file) {
        catchExc(
                () -> Files.createFile(file),
                String.format(COULD_NOT_CREATE_FILE, file.toString()),
                file.getFileName().toString()
        );

        doUpdate(r, file);
    }

    @Override
    protected Resume doGet(Path file) {
        return catchExc(
                () -> serializer.doRead(new BufferedInputStream(Files.newInputStream(file))),
                FILE_READ_ERROR, file.getFileName().toString()
        );
    }

    @Override
    protected void doUpdate(Resume r, Path file) {
        catchExc(
                () -> serializer.doWrite(r, new BufferedOutputStream(Files.newOutputStream(file))),
                FILE_WRITE_ERROR, r.getUuid()
        );
    }

    @Override
    protected void doDelete(Path file) {
        executeAndValidate(
                () -> Files.deleteIfExists(file),
                result -> result,
                String.format(COULD_NOT_DELETE_FILE, file),
                file.getFileName().toString()
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

    private Stream<Path> getAll() {
        return catchExc(
                () -> Files.list(directory), DIRECTORY_READ_ERROR, null
        );
    }
}