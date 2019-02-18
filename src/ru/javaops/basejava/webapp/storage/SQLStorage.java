package ru.javaops.basejava.webapp.storage;

import ru.javaops.basejava.webapp.exception.ExistStorageException;
import ru.javaops.basejava.webapp.exception.NotExistStorageException;
import ru.javaops.basejava.webapp.exception.StorageException;
import ru.javaops.basejava.webapp.model.Resume;
import ru.javaops.basejava.webapp.sql.ConnectionFactory;

import java.sql.*;
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
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO resume (uuid, full_name) VALUES (?, ?) ON CONFLICT DO NOTHING")) {
            ps.setString(1, r.getUuid());
            ps.setString(2, r.getFullName());
            if (ps.executeUpdate() == 0) {
                throw new ExistStorageException(r.getUuid());
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public Resume get(String uuid) {
        Resume result;
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM resume r WHERE r.uuid = ?")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new NotExistStorageException(uuid);
            }
            result = new Resume(uuid, rs.getString("full_name"));
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return result;
    }

    @Override
    public void update(Resume r) {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE resume set full_name = ? where uuid = ?")) {
            ps.setString(2, r.getUuid());
            ps.setString(1, r.getFullName());
            if (ps.executeUpdate() == 0) {
                throw new NotExistStorageException(r.getUuid());
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void delete(String uuid) {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM resume r WHERE r.uuid = ?")) {
            ps.setString(1, uuid);
            if (ps.executeUpdate() == 0) {
                throw new NotExistStorageException(uuid);
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public void clear() {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM resume")) {
                ps.execute();
        } catch (SQLException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public List<Resume> getAllSorted() {
        List<Resume> result = new ArrayList<>();
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM resume")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new Resume(rs.getString("uuid"), rs.getString("full_name")));
            }
        } catch (SQLException e) {
            throw new StorageException(e);
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public int size() {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) AS total FROM resume")) {
            ps.execute();
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            throw new StorageException(e);
        }
        return 0;
    }
}
