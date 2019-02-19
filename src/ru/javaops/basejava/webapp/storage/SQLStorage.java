package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.exception.ExistStorageException;
import ru.javaops.basejava.webapp.exception.NotExistStorageException;
import ru.javaops.basejava.webapp.model.Resume;
import ru.javaops.basejava.webapp.sql.SQLHelper;

import java.sql.DriverManager;
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
    private final SQLHelper helper;

    public SQLStorage(String dbUrl, String dbUser, String dbPassword) {
        this.helper = new SQLHelper(() -> DriverManager.getConnection(dbUrl, dbUser, dbPassword));
    }

    @Override
    public void save(Resume r) {
        helper.execute(
                "INSERT INTO resume (uuid, full_name) VALUES (?, ?)",
                ps -> {
                    ps.setString(1, r.getUuid());
                    ps.setString(2, r.getFullName());
                    ps.execute();
                    return null;
                }
        );
    }

    @Override
    public Resume get(String uuid) {
        return helper.execute(
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
        int res = helper.execute(
                "UPDATE resume set full_name = ? where uuid = ?",
                ps -> {
                    ps.setString(1, r.getFullName());
                    ps.setString(2, r.getUuid());
                    return ps.executeUpdate();
                }
        );
        checkNonExist(res, r.getUuid());
    }

    @Override
    public void delete(String uuid) {
        int res = helper.execute(
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
        helper.execute("DELETE FROM resume");
    }

    @Override
    public List<Resume> getAllSorted() {
        return helper.execute(
                "SELECT * FROM resume ORDER BY full_name, uuid",
                ps -> {
                    ResultSet rs = ps.executeQuery();
                    List<Resume> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(new Resume(rs.getString("uuid"), rs.getString("full_name")));
                    }
                    return result;
                }
        );
    }

    @Override
    public int size() {
        return helper.execute(
                "SELECT COUNT(*) AS total FROM resume",
                ps -> {
                    ps.execute();
                    ResultSet rs = ps.executeQuery();
                    return rs.next() ? rs.getInt(1) : 0;
                }
        );
    }

    private void checkNonExist(int rowCount, String uuid) {
        if (rowCount == 0) {
            throw new NotExistStorageException(uuid);
        }
    }
}
