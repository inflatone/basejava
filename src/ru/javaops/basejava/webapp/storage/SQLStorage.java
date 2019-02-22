package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.exception.NotExistStorageException;
import ru.javaops.basejava.webapp.model.*;
import ru.javaops.basejava.webapp.sql.SQLHelper;

import java.sql.*;
import java.util.*;

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
        final Resume resume = helper.execute(
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
        helper.execute(
                "  SELECT * FROM section s " +
                        "WHERE s.resume_uuid = ?",
                ps -> {
                    ps.setString(1, uuid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        addSection(rs, resume);
                    }
                    return null;
                }
        );
        return resume;
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
        final Map<String, Resume> result =  helper.execute(
                "       SELECT * FROM resume r " +
                        "LEFT JOIN contact c " +
                        "       ON r.uuid = c.resume_uuid " +
                        " ORDER BY r.full_name, r.uuid",
                ps -> grabResumes(ps.executeQuery())
        );
        helper.execute(
                "SELECT * FROM section s ",
                ps -> {
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        addSection(rs, result.get(rs.getString("resume_uuid")));
                    }
                    return null;
                }
        );
        return new ArrayList<>(result.values());
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
        simpleExecuteByUuid(connection, "DELETE FROM contact c WHERE c.resume_uuid = ?", r.getUuid());
    }

    private void insertSections(Connection connection, Resume r) throws SQLException {
        helper.executePreparedStatement(connection, "INSERT INTO section (resume_uuid, type, value) VALUES (?, ?, ?)", ps -> {
            for (Map.Entry<SectionType, Section> s : r.getSections().entrySet()) {
                ps.setString(1, r.getUuid());
                ps.setString(2, s.getKey().name());
                switch (s.getKey()) {
                    case OBJECTIVE:
                    case PERSONAL:
                        ps.setString(3, ((TextSection) s.getValue()).getContent());
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        ps.setString(3, String.join("\n", ((ListSection) s.getValue()).getItems()));
                        break;
                    case EXPERIENCE:
                    case EDUCATION:
                        insertOrganizationSection(ps, (OrganizationSection) s.getValue());
                }
                ps.addBatch();
            }
            ps.executeBatch();
            return null;
        });
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

    private void insertOrganizationSection(PreparedStatement ps, OrganizationSection section) throws SQLException {

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
            switch (type) {
                case PERSONAL:
                case OBJECTIVE:
                    resume.addSection(type, new TextSection(value));
                    break;
                case ACHIEVEMENT:
                case QUALIFICATIONS:
                    resume.addSection(type, new ListSection(value.split("\n")));
                    break;
                case EXPERIENCE:
                case EDUCATION:
                    resume.addSection(type, new OrganizationSection(grabOrganizations(value)));
            }
        }
    }

    private List<Organization> grabOrganizations(String value) {
        return Collections.emptyList();
    }

    private Map<String, Resume> grabResumes(ResultSet rs) throws SQLException {
        Map<String, Resume> map = new LinkedHashMap<>();
        while (rs.next()) {
            String uuid = rs.getString("uuid");
            Resume r = map.get(uuid);
            if (r == null) {
                r= new Resume(uuid, rs.getString("full_name"));
                map.put(uuid, r);
            }
            addContact(rs, r);
        }
        return map;
    }
}
