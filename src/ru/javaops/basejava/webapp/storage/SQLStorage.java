package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.exception.NotExistStorageException;
import ru.javaops.basejava.webapp.model.*;
import ru.javaops.basejava.webapp.sql.SQLHelper;
import ru.javaops.basejava.webapp.util.JsonParser;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
            insertSections(connection, r);
            return null;
        });
    }

    @Override
    public Resume get(String uuid) {
        return helper.transactionalExecute(
                connection -> {
                    final Resume r = getResume(connection, uuid);
                    fillSections(connection, r);
                    return r;
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
            deleteSections(connection, r);
            insertSections(connection, r);
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
        return helper.transactionalExecute(
                connection -> {
                    final Map<String, Resume> resumes = getResumes(connection);
                    fillContacts(connection, resumes);
                    fillSections(connection, resumes);
                    return new ArrayList<>(resumes.values());
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

    private void insertSections(Connection connection, Resume r) throws SQLException {
        helper.executePreparedStatement(
                connection,
                "INSERT INTO section (resume_uuid, type, value) VALUES (?, ?, ?)",
                ps -> {
                    for (Map.Entry<SectionType, Section> s : r.getSections().entrySet()) {
                        ps.setString(1, r.getUuid());
                        ps.setString(2, s.getKey().name());
                        ps.setString(3, JsonParser.write(s.getValue(), Section.class));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    return null;
                });
    }

    private void deleteContacts(Connection connection, Resume r) throws SQLException {
        simpleExecuteByUuid(connection, "DELETE FROM contact c WHERE c.resume_uuid = ?", r.getUuid());
    }

    private void deleteSections(Connection connection, Resume r) throws SQLException {
        simpleExecuteByUuid(connection, "DELETE FROM section c WHERE c.resume_uuid = ?", r.getUuid());
    }

    private void simpleExecuteByUuid(Connection connection, String sql, String uuid) throws SQLException {
        helper.executePreparedStatement(connection, sql, ps -> {
            ps.setString(1, uuid);
            ps.execute();
            return null;
        });
    }

    private void addContact(ResultSet rs, Resume resume) throws SQLException {
        String value = rs.getString("value");
        if (value != null) {
            resume.addContact(
                    ContactType.valueOf(rs.getString("type")),
                    value
            );
        }
    }

    private void addSection(ResultSet rs, Resume resume) throws SQLException {
        String value = rs.getString("value");
        if (value != null) {
            SectionType type = SectionType.valueOf(rs.getString("type"));
            resume.addSection(type, JsonParser.read(value, Section.class));
        }
    }

    private Resume getResume(Connection connection, String uuid) throws SQLException {
        return helper.executePreparedStatement(
                connection,
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

    private Map<String, Resume> getResumes(Connection connection) throws SQLException {
        return helper.executePreparedStatement(
                connection,
                "SELECT * FROM resume r ORDER BY r.full_name, r.uuid",
                ps -> {
                    Map<String, Resume> map = new LinkedHashMap<>();
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        String uuid = rs.getString("uuid");
                        map.put(uuid, new Resume(uuid, rs.getString("full_name")));
                    }
                    return map;
                }
        );
    }

    private void fillSections(Connection connection, Resume resume) throws SQLException {
        helper.executePreparedStatement(
                connection,
                "   SELECT * FROM section s WHERE s.resume_uuid = ?",
                ps -> {
                    ps.setString(1, resume.getUuid());
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        addSection(rs, resume);
                    }
                    return null;
                }
        );
    }

    private void fillSections(Connection connection, Map<String, Resume> map) throws SQLException {
        //language=PostgreSQL
        fill(connection, "SELECT * FROM section s ", map, this::addSection);
    }

    private void fillContacts(Connection connection, Map<String, Resume> map) throws SQLException {
        //language=PostgreSQL
        fill(connection, "SELECT * FROM contact s ", map, this::addContact);
    }

    private void fill(Connection connection, String sql, Map<String, Resume> map, ElementProcessor filler) throws SQLException {
        helper.executePreparedStatement(connection, sql, ps -> {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                filler.fill(rs, map.get(rs.getString("resume_uuid")));
            }
            return null;
        });
    }

    private interface ElementProcessor {
        void fill(ResultSet rs, Resume resume) throws SQLException;
    }
}
