package ru.javaops.basejava.webapp;

import ru.javaops.basejava.webapp.storage.SQLStorage;
import ru.javaops.basejava.webapp.storage.Storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides getting extra app properties loading them from disk.
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-02-18
 */
public class Config {
    private static final String PROPS = "/resumes.properties";
    private static final Config INSTANCE = new Config();
    private final String storageDir;
    private final Storage storage;

    private Config() {
        try (InputStream is = Config.class.getResourceAsStream(PROPS)) {
            Properties props = new Properties();
            props.load(is);
            storageDir = props.getProperty("storage.dir");
            storage = new SQLStorage(
                    props.getProperty("db.url"), props.getProperty("db.user"), props.getProperty("db.password")
            );
        } catch (IOException e) {
            throw new IllegalStateException("Invalid config file " + PROPS);
        }
    }

    public static Config get() {
        return INSTANCE;
    }


    public String getStorageDir() {
        return storageDir;
    }

    public Storage getStorage() {
        return storage;
    }
}
