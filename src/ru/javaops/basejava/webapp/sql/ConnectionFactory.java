package ru.javaops.basejava.webapp.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base for implementing providing connections to DB
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-18
 */
public interface ConnectionFactory {
    Connection getConnection() throws SQLException;
}
