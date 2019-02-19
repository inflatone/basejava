package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.exception.ExistStorageException;
import ru.javaops.basejava.webapp.exception.NotExistStorageException;
import ru.javaops.basejava.webapp.model.Resume;
import ru.javaops.basejava.webapp.sql.ConnectionFactory;
import ru.javaops.basejava.webapp.util.SQLHelper;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Storage based on SQL connections
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-18
 */
public class SQLStorage implements Storage {
    private final ConnectionFactory connectionFactory;

    public SQLStorage(String dbUrl, String dbUser, String dbPassword) {
        connectionFactory = () -> DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    @Override
    public void save(Resume r) {
        int rowCount = SQLHelper.help(
                connectionFactory,
                "INSERT INTO resume (uuid, full_name) VALUES (?, ?) ON CONFLICT DO NOTHING",
                ps -> {
                    ps.setString(1, r.getUuid());
                    ps.setString(2, r.getFullName());
                    return ps.executeUpdate();
                }
        );
        checkExist(rowCount, r.getUuid());
    }

    @Override
    public Resume get(String uuid) {
        return SQLHelper.help(
                connectionFactory,
                "SELECT * FROM resume r WHERE r.uuid = ?",
                ps -> {
                    ps.setString(1, uuid);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new NotExistStorageException(uuid);
                    }
                    return new Resume(uuid, rs.getString("full_name"));
                }
        );
    }

    @Override
    public void update(Resume r) {
        int res = SQLHelper.help(
                connectionFactory,
                "UPDATE resume set full_name = ? where uuid = ?",
                ps -> {
                    ps.setString(2, r.getUuid());
                    ps.setString(1, r.getFullName());
                    return ps.executeUpdate();
                }
        );
        checkNonExist(res, r.getUuid());
    }

    @Override
    public void delete(String uuid) {
        int res = SQLHelper.help(
                connectionFactory,
                "DELETE FROM resume r WHERE r.uuid = ?",
                ps -> {
                    ps.setString(1, uuid);
                    return ps.executeUpdate();
                }
        );
        checkNonExist(res, uuid);
    }

    @Override
    public void clear() {
        SQLHelper.help(connectionFactory, "DELETE FROM resume", PreparedStatement::execute);
    }

    @Override
    public List<Resume> getAllSorted() {
        return SQLHelper.help(
                connectionFactory,
                "SELECT * FROM resume",
                ps -> {
                    ResultSet rs = ps.executeQuery();
                    List<Resume> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(new Resume(rs.getString("uuid"), rs.getString("full_name")));
                    }
                    Collections.sort(result);
                    return result;
                }
        );
    }

    @Override
    public int size() {
        return SQLHelper.help(
                connectionFactory,
                "SELECT COUNT(*) AS total FROM resume",
                ps -> {
                    int result = 0;
                    ps.execute();
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        result = rs.getInt("total");
                    }
                    return result;
                }
        );
    }

    private void checkExist(int rowCount, String uuid) {
        if (rowCount == 0) {
            throw new ExistStorageException(uuid);
        }
    }

    private void checkNonExist(int rowCount, String uuid) {
        if (rowCount == 0) {
            throw new NotExistStorageException(uuid);
        }
    }
}
