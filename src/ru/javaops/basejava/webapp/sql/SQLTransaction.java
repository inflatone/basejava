package ru.javaops.basejava.webapp.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base for implementing providing transactions to DB
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-20
 */
public interface SQLTransaction<T> {
    T execute(Connection connection) throws SQLException;
}
