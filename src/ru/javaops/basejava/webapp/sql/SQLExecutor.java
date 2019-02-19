package ru.javaops.basejava.webapp.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Functional interface with throwing {@link SQLException} to execute sql queers
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-20
 */
public interface SQLExecutor<T> {
    T execute(PreparedStatement ps) throws SQLException;
}
