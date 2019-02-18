package ru.javaops.basejava.webapp;

import java.io.File;
import java.io.FileInputStream;
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
    static final File PROPS = new File("./config/resumes.properties");
    private static Config INSTANCE = new Config();
    private Properties props = new Properties();
    private String storageDir;

    private Config() {
        try (InputStream is = new FileInputStream(PROPS)) {
            props.load(is);
            storageDir = props.getProperty("storage.dir");
        } catch (IOException e) {
            throw new IllegalStateException("Invalid config file " + PROPS.getAbsolutePath());
        }
    }

    public static Config get() {
        return INSTANCE;
    }

    public String getStorageDir() {
        return storageDir;
    }

    public String getDbUrl() {
        return props.getProperty("db.url");
    }
    public String getDbUser() {
        return props.getProperty("db.user");
    }

    public String getDbPassword() {
        return props.getProperty("db.password");
    }

}
