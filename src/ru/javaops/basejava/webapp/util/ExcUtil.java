package ru.javaops.basejava.webapp.util;

import ru.javaops.basejava.webapp.exception.StorageException;

import java.io.IOException;

/**
 * Utility methods for dealing with exceptions
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-16
 */
public class ExcUtil {
    /**
     * Helps get rid of any checked exceptions in lambda expression by wrapping them in unchecked {@link StorageException}
     *
     * @param throwing operation throwing checked exception
     * @param message  message to own unchecked exception
     * @param uuid     uuid of resume caused throwing exception
     */
    public static <T> T catchExc(UnaryEx<T> throwing, String message, String uuid) {
        try {
            return throwing.action();
        } catch (IOException e) {
            throw new StorageException(message, uuid, e);
        }
    }

    /**
     * Function interface with operation throwing checked {@link IOException}.
     * <p>
     * https://github.com/peterarsentev/code_quality_principles#4-dont-use-exceptions-exceptions-make-your-code-ugly
     */
    public interface UnaryEx<T> {
        T action() throws IOException;
    }
}
