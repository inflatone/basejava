package ru.javaops.basejava.webapp.sql;

import org.postgresql.util.PSQLException;
import ru.javaops.basejava.webapp.exception.ExistStorageException;
import ru.javaops.basejava.webapp.exception.StorageException;

import java.sql.SQLException;

/**
 * Utility class to handle SQLException
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-20
 */
public class ExceptionUtil {
    public static StorageException convertException(SQLException e) {
        if (e.getClass() == PSQLException.class) {
            //http://www.postgresql.org/docs/9.3/static/errcodes-appendix.html
            if (e.getSQLState().equals("23505")) {
                return new ExistStorageException(null);
            }
        }
        return new StorageException(e);
    }
}
