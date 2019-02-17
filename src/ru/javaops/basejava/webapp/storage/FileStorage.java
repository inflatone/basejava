package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.storage.serializer.StreamSerializer;
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
public class FileStorage extends AbstractSerializedStorage<File> {
    private final File directory;

    public FileStorage(String directory, StreamSerializer serializer) {
        super(serializer);
        this.directory = ValidateUtil.validateAndGetDirectoryFile(directory);
    }

    @Override
    protected InputStream newIn(File file) throws IOException {
        return new FileInputStream(file);
    }

    @Override
    protected OutputStream newOut(File file) throws IOException {
        return new FileOutputStream(file);
    }

    @Override
    protected boolean doCreateFile(File file) throws IOException {
        return file.createNewFile();
    }

    @Override
    protected boolean doDeleteFile(File file) {
        return file.delete();
    }

    @Override
    protected String getFileName(File file) {
        return file.getName();
    }

    @Override
    protected File getSearchKey(String uuid) {
        return new File(directory, uuid);
    }

    @Override
    protected boolean isExist(File file) {
        return file.exists();
    }

    @Override
    protected Stream<File> getAll() {
        return Arrays.stream(getAll(File::listFiles));
    }

    private <T> T[] getAll(Function<File, T[]> directoryFlatMapper) {
        return executeAndValidate(
                () -> directoryFlatMapper.apply(directory),
                Objects::nonNull,
                DIRECTORY_READ_ERROR
        );
    }
}