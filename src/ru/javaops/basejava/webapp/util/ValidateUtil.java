package ru.javaops.basejava.webapp.util;

import ru.javaops.basejava.webapp.exception.StorageException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility methods for validating data
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-17
 */
public class ValidateUtil {
    private static final Map<DirectoryErrorState, Predicate<Path>> PATH_VALIDATORS
            = new EnumMap<>(DirectoryErrorState.class);
    private static final Map<DirectoryErrorState, Predicate<File>> FILE_VALIDATORS
            = new EnumMap<>(DirectoryErrorState.class);

    static {
        PATH_VALIDATORS.put(DirectoryErrorState.NOT_EXIST, Files::exists);
        PATH_VALIDATORS.put(DirectoryErrorState.NOT_DIRECTORY, Files::isDirectory);
        PATH_VALIDATORS.put(DirectoryErrorState.NOT_READBLE, Files::isReadable);
        PATH_VALIDATORS.put(DirectoryErrorState.NOT_WRITABLE, Files::isWritable);

        FILE_VALIDATORS.put(DirectoryErrorState.NOT_EXIST, File::exists);
        FILE_VALIDATORS.put(DirectoryErrorState.NOT_DIRECTORY, File::isDirectory);
        FILE_VALIDATORS.put(DirectoryErrorState.NOT_READBLE, File::canRead);
        FILE_VALIDATORS.put(DirectoryErrorState.NOT_WRITABLE, File::canWrite);
    }

    public static Path validateAndGetDirectoryPath(String pathName) {
        return validateDirectory(pathName, name -> Paths.get(name), file -> file.toAbsolutePath().toString(), PATH_VALIDATORS);
    }

    public static File validateAndGetDirectoryFile(String fileName) {
        return validateDirectory(fileName, File::new, File::getAbsolutePath, FILE_VALIDATORS);
    }

    public static <T> T executeAndValidate(
            ExcUtil.UnaryEx<T> operation, Predicate<T> validator, String excMessage, String excUuid
    ) {
        T result = ExcUtil.catchExc(operation, excMessage, excUuid);
        if (!validator.test(result)) {
            throw new StorageException(excMessage, excUuid);
        }
        return result;
    }

    private static <T> T validateDirectory(
            String fileName, Function<String, T> mapper,
            Function<T, String> absolutizer, Map<DirectoryErrorState, Predicate<T>> validators
    ) {
        Objects.requireNonNull(fileName);
        T result = mapper.apply(fileName);
        validators.forEach((k, v) -> {
            if (!v.test(result)) {
                throw new IllegalArgumentException(String.format("%s %s", absolutizer.apply(result), k.message));
            }
        });
        return result;
    }

    private enum DirectoryErrorState {
        NOT_EXIST("does not exist"),
        NOT_DIRECTORY("is not directory"),
        NOT_READBLE("is not readable"),
        NOT_WRITABLE("is not writable");

        private String message;

        DirectoryErrorState(String message) {
            this.message = message;
        }
    }
}
