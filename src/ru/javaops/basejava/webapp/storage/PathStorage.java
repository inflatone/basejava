package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.model.Resume;
import ru.javaops.basejava.webapp.storage.serializer.StreamSerializer;
import ru.javaops.basejava.webapp.util.ValidateUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static ru.javaops.basejava.webapp.util.ExcUtil.catchExc;

/**
 * Path based storage for Resumes
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-16
 */
public class PathStorage extends AbstractSerializedStorage<Path> {
    private final Path directory;

    public PathStorage(String directory, StreamSerializer serializer) {
        super(serializer);
        this.directory = ValidateUtil.validateAndGetDirectoryPath(directory);
    }

    @Override
    protected InputStream newIn(Path file) throws IOException {
        return Files.newInputStream(file);
    }

    @Override
    protected OutputStream newOut(Path file) throws IOException {
        return Files.newOutputStream(file);
    }

    @Override
    protected boolean doCreateFile(Path file) throws IOException {
        Files.createFile(file);
        return true;
    }

    @Override
    protected boolean doDeleteFile(Path file) throws IOException {
        return Files.deleteIfExists(file);
    }

    @Override
    protected String getFileName(Path file) {
        return file.getFileName().toString();
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
    protected Stream<Resume> doGetAllStream() {
        return getAll().map(this::doGet);
    }

    @Override
    protected Stream<Path> getAll() {
        return catchExc(() -> Files.list(directory), DIRECTORY_READ_ERROR);
    }
}