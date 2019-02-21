package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.exception.NotExistStorageException;
import ru.javaops.basejava.webapp.model.ContactType;
import ru.javaops.basejava.webapp.model.Resume;
import ru.javaops.basejava.webapp.sql.SQLHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        helper.transactionalExecute(connection -> {
            helper.executePreparedStatement(connection, "INSERT INTO resume (uuid, full_name) VALUES (?, ?)", ps -> {
                ps.setString(1, r.getUuid());
                ps.setString(2, r.getFullName());
                ps.execute();
                return null;
            });
            insertContacts(connection, r);
            return null;
        });
    }

    @Override
    public Resume get(String uuid) {
        return helper.execute(
                "       SELECT * FROM resume r " +
                        "LEFT JOIN contact c " +
                        "       ON r.uuid = c.resume_uuid " +
                        "    WHERE r.uuid = ?",
                ps -> {
                    ps.setString(1, uuid);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new NotExistStorageException(uuid);
                    }
                    Resume result = new Resume(uuid, rs.getString("full_name"));
                    do {
                        addContact(rs, result);
                    } while (rs.next());
                    return result;
                }
        );
    }

    @Override
    public void update(Resume r) {
        helper.transactionalExecute(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE resume SET full_name = ? WHERE uuid = ?"
            )) {
                ps.setString(1, r.getFullName());
                ps.setString(2, r.getUuid());
                checkNonExist(ps.executeUpdate(), r.getUuid());
            }
            deleteContacts(connection, r);
            insertContacts(connection, r);
            return null;
        });
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
                "       SELECT * FROM resume r " +
                        "LEFT JOIN contact c " +
                        "       ON r.uuid = c.resume_uuid " +
                        " ORDER BY r.full_name, r.uuid",
                ps -> grabResumes(ps.executeQuery())
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

    private void insertContacts(Connection connection, Resume r) throws SQLException {
        helper.executePreparedStatement(connection, "INSERT INTO contact (resume_uuid, type, value) VALUES (?, ?, ?)", ps -> {
            for (Map.Entry<ContactType, String> c : r.getContacts().entrySet()) {
                ps.setString(1, r.getUuid());
                ps.setString(2, c.getKey().name());
                ps.setString(3, c.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
            return null;
        });
    }

    private void deleteContacts(Connection connection, Resume r) throws SQLException {
        helper.executePreparedStatement(connection, "DELETE FROM contact c WHERE c.resume_uuid = ?", ps -> {
            ps.setString(1, r.getUuid());
            ps.execute();
            return null;
        });
    }

    private void addContact(ResultSet rs, Resume resume) throws SQLException {
        String type = rs.getString("type");
        if (type != null) {
            resume.addContact(
                    ContactType.valueOf(type),
                    rs.getString("value")
            );
        }
    }

    private List<Resume> grabResumes(ResultSet rs) throws SQLException {
        List<Resume> result = new ArrayList<>();
        Resume next = null;
        String uuid = null;
        while (rs.next()) {
            String currentUuid = rs.getString("uuid");
            if (!currentUuid.equals(uuid)) {
                next = new Resume(currentUuid, rs.getString("full_name"));
                result.add(next);
                uuid = currentUuid;
            }
            addContact(rs, next);
        }
        return result;
    }
}
