package ru.javaops.basejava.webapp.util;

import ru.javaops.basejava.webapp.exception.StorageException;
import ru.javaops.basejava.webapp.sql.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Utility class for handling sql connections
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-18
 */
public class SQLHelper {
    public static <T> T help(ConnectionFactory factory, String statement, SQLExecutor<T> executor) {
        try (Connection connection = factory.getConnection();
             PreparedStatement ps = connection.prepareStatement(statement)) {
            return executor.execute(ps);
        } catch (SQLException e) {
            throw new StorageException(e);
        }
    }

    public interface SQLExecutor<T> {
        T execute(PreparedStatement ps) throws SQLException;
    }
}
